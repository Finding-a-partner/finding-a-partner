package com.finding_a_partner.message_service.errors

import org.springframework.http.HttpStatus

class NotEnoughRightsException() : ApiError(
    status = HttpStatus.BAD_REQUEST,
    message = "Недосаточно прав для совершения запроса",
    debugMessage = "Not enough rights to make the request",
)