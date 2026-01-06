package com.findingpartners.user_service.errors


import org.springframework.http.HttpStatus

class AlreadyExistsException(
    data: Any? = null,
) : ApiError(
    status = HttpStatus.BAD_REQUEST,
    message = "Уже существует" + if (data == null) "" else " [$data]",
    debugMessage = "Already exists" + if (data == null) "" else " [$data]",
)
