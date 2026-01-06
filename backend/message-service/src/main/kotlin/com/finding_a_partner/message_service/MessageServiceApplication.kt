package com.finding_a_partner.message_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
class MessageServiceApplication

fun main(args: Array<String>) {
    runApplication<MessageServiceApplication>(*args)
}
