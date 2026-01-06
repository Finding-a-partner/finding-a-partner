package com.finding_a_partner.group_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
class GroupServiceApplication

fun main(args: Array<String>) {
    runApplication<GroupServiceApplication>(*args)
}
