package com.findingpartners.user_service.errors

import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.findingpartners.user_service.logging.FreelancingLogger

@JsonIgnoreProperties("localizedMessage", "cause", "stackTrace", "suppressed")
open class ApiError(
    override var message: String,
) : Exception() {
    var status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    var debugMessage: String = status.name

    @JsonInclude(JsonInclude.Include.NON_EMPTY)

    @JsonFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss[.SSS]")
    val timestamp: LocalDateTime = LocalDateTime.now()

    private var logger: FreelancingLogger = FreelancingLogger(this::class.java)

    constructor(
        status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        message: String,
        debugMessage: String = status.name,
    ) : this(message) {
        this.status = status
        this.debugMessage = debugMessage
    }

    constructor(
        status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        message: String,
        exception: Throwable?,
        debugMessage: String = exception?.message ?: status.name,
    ) : this(status, message, debugMessage) {
        logger.error("Error with Status Code \"${status.value()}\":\n ${exception?.stackTraceToString()}")
    }
}