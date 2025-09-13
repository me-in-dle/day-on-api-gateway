package com.gateway.config

import com.gateway.utils.HeaderType
import org.springframework.http.server.reactive.ServerHttpRequest

interface AuthorizationConfig {

    fun validateAuthorizationHeaderAndGetAccessToken(request: ServerHttpRequest): String

    fun validateAuthorizationHeaderAndGetRefreshToken(request: ServerHttpRequest): String

    fun parserUserId(token: String, headerType: HeaderType): String

}