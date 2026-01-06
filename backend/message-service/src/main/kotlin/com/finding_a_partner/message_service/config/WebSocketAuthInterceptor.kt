package com.finding_a_partner.message_service.config

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

class WebSocketAuthInterceptor : HandshakeInterceptor, ChannelInterceptor {

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        if (request is ServletServerHttpRequest) {
            val servletRequest = request.servletRequest

            val authHeader = servletRequest.getHeader("Authorization")
            val userIdHeader = servletRequest.getHeader("X-User-Id")
            
            if (userIdHeader != null) {
                return true
            }
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return true
            }

            return true
        }
        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
    }

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
        
        if (accessor != null) {
            val command = accessor.command
            val destination = accessor.destination
            
            when (command) {
                StompCommand.CONNECT -> {
                    val authHeader = accessor.getFirstNativeHeader("Authorization")
                    val userIdHeader = accessor.getFirstNativeHeader("X-User-Id")
                    if (userIdHeader != null) {
                        accessor.setUser { userIdHeader }
                    }
                }
                StompCommand.SEND -> {
                    val messageBody = message.payload

                    val headers = accessor.toMap()
                }
                StompCommand.SUBSCRIBE -> {
                }
                else -> {
                    println("[WebSocketAuthInterceptor] Other command: $command")
                }
            }
        } else {
            println("[WebSocketAuthInterceptor] preSend - accessor is null")
        }
        
        return message
    }
}

