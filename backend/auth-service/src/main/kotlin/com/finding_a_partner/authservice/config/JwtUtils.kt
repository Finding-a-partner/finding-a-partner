package com.finding_a_partner.authservice.config

import com.finding_a_partner.authservice.feign.UserResponse
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

object PemUtils {

    fun decodePem(pemBytes: ByteArray): ByteArray {
        val pemString = String(pemBytes)
            .replace("-----BEGIN (.*)-----".toRegex(), "")
            .replace("-----END (.*)-----".toRegex(), "")
            .replace("\\s".toRegex(), "") // удаляет пробелы и переносы строк

        return Base64.getDecoder().decode(pemString)
    }
}

@Configuration
class RsaKeyConfig {

    @Bean
    fun keyPair(): KeyPair {
        val privateKey = loadPrivateKey("keys/private.pem")
        val publicKey = loadPublicKey("keys/public.pem")
        return KeyPair(publicKey, privateKey)
    }

    private fun loadPrivateKey(path: String): PrivateKey {
        val resource = javaClass.classLoader.getResource(path)
            ?: throw IllegalArgumentException("File not found: $path")
        val keyBytes = Files.readAllBytes(Paths.get(javaClass.classLoader.getResource(path)!!.toURI()))
        val spec = PKCS8EncodedKeySpec(PemUtils.decodePem(keyBytes))
        return KeyFactory.getInstance("RSA").generatePrivate(spec)
    }

    private fun loadPublicKey(path: String): PublicKey {
        val resource = javaClass.classLoader.getResource(path)
            ?: throw IllegalArgumentException("File not found: $path")
        val keyBytes = Files.readAllBytes(Paths.get(javaClass.classLoader.getResource(path)!!.toURI()))
        val spec = X509EncodedKeySpec(PemUtils.decodePem(keyBytes))
        return KeyFactory.getInstance("RSA").generatePublic(spec)
    }
}

@Configuration
class JwtJwkConfig(
    private val keyPair: KeyPair,
) {

    @Bean
    fun jwkSet(): JWKSet {
        val publicKey = keyPair.public as RSAPublicKey

        val rsaKey = RSAKey.Builder(publicKey)
            .keyID("auth-key")
            .build()

        return JWKSet(rsaKey)
    }
}

@RestController
class JwksController(
    private val jwkSet: JWKSet,
) {
    @GetMapping("/.well-known/jwks.json")
    fun keys(): Map<String, Any> {
        return jwkSet.toJSONObject()
    }
}

@Component
class JwtUtils(
    private val keyPair: KeyPair,
) {
    val privateKey = keyPair.private
    private val jwtExpirationInMs: Long = 15 * 60 * 1000

    fun generateToken(username: String, userId: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs)

        return Jwts.builder()
            .setSubject(username)
            .claim("userId", userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .setHeaderParam("kid", "auth-key")
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact()
    }

    fun extractUsername(token: String): String {
        return getClaimsFromToken(token).subject
    }

    fun extractUserId(token: String): String {
        val claims = getClaimsFromToken(token)
        return claims["userId"] as String
    }

    fun isTokenExpired(token: String): Boolean {
        return getClaimsFromToken(token).expiration.before(Date())
    }

    fun validateToken(token: String, username: String): Boolean {
        val tokenUsername = extractUsername(token)
        return (tokenUsername == username && !isTokenExpired(token))
    }

    private fun getClaimsFromToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(keyPair.public)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
