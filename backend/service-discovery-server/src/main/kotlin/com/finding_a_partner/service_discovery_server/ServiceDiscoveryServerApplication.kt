package com.finding_a_partner.service_discovery_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class ServiceDiscoveryServerApplication

fun main(args: Array<String>) {
    runApplication<ServiceDiscoveryServerApplication>(*args)
}
