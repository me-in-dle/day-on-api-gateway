package com.gateway.api.exception.exception

class GatewayException(
    private val code: GatewayExceptionCode,
) : RuntimeException(code.message) {

    override val cause: Throwable = Throwable(code.errorCode)
    val status: Int = code.status
    val errorCode: String = code.errorCode
}