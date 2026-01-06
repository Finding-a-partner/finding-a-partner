package com.findingpartners.user_service.errors

import org.springframework.http.HttpStatus

class ResourceNotFoundException(
    data: Any? = null
) : ApiError(
    status = HttpStatus.NOT_FOUND,
    message = "Такого ресурса не существует" + if (data == null) "" else " [$data]",
    debugMessage = "Resource not found" + if (data == null) "" else " [$data]"
)