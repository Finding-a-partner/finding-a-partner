package com.finding_a_partner.authservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/actuator/**",
                        "/actuator",
                        "/actuator/*",
                        "/actuator/*/**"
                    ).permitAll()
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/.well-known/**").permitAll()
                    .anyRequest().authenticated()
  //                  .anyRequest().permitAll()
            }
        return http.build()
    }
}
