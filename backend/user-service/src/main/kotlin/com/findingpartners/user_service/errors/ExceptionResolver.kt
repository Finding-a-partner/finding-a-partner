package com.findingpartners.user_service.errors

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class ExceptionResolver {

    @ExceptionHandler(Exception::class)
    fun resolveException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: Exception,
    ) {
        val exceptionToResponse = toApiError(exception)
        val objectMapper = ObjectMapper().findAndRegisterModules()
        response.contentType = MediaType.APPLICATION_JSON.toString()
        response.status = exceptionToResponse.status.value()
        response.characterEncoding = "UTF-8"
        response.writer.write(objectMapper.writeValueAsString(exceptionToResponse))
    }

    private fun toApiError(exception: Exception): ApiError {
        if (exception is ApiError) return exception
        if (exception is MethodArgumentNotValidException) {
            var message = ""
            for (error in exception.fieldErrors) {
                message += "${error.field}: ${error.defaultMessage};       "
            }
            return ApiError(message = message, status = HttpStatus.BAD_REQUEST)
        }
        return ApiError(message = exception.message.orEmpty(), debugMessage = exception.localizedMessage.orEmpty())
    }
}
