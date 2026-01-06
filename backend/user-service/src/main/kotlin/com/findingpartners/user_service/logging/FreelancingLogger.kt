package com.findingpartners.user_service.logging

import org.slf4j.LoggerFactory

class FreelancingLogger(clazz: Class<*>?) {
    private val logger = LoggerFactory.getLogger(clazz)

    fun error(msg: String?) {
        logger.error("\u001B[31m$msg\u001B[0m")
    }

    fun error(msg: String?, throwable: Throwable) {
        logger.error("\u001B[31m$msg\u001B[0m", throwable)
    }

    fun info(msg: String?) {
        logger.info("\u001B[32m$msg\u001B[0m")
    }
}