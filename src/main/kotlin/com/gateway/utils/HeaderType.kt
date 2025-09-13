package com.gateway.utils

enum class HeaderType(
    val type: String,
) {
    AUTHORIZATION_HEADER("Authorization"),
    REFRESHTOKEN_HEADER("RefreshToken"),
    SEC_WEBSOCKET_PROTOCOL("Sec-WebSocket-Protocol")
}