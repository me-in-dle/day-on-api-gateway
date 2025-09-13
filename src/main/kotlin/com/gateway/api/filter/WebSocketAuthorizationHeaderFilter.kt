package com.gateway.api.filter

import com.fasterxml.jackson.core.JsonProcessingException
import com.gateway.api.exception.exception.GatewayException
import com.gateway.api.exception.exception.GatewayExceptionCode
import com.gateway.config.AuthorizationConfig
import com.gateway.domain.port.CachePort
import com.gateway.utils.Logger
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.stereotype.Component

@Component
class WebSocketAuthorizationHeaderFilter(
    private val cachePort: CachePort,
    private val errorFilter: ErrorFilter,
) : AbstractGatewayFilterFactory<AuthorizationConfig>() {

    companion object : Logger() {
        private const val WS_TICKET_HEADER = "WS-TICKET"
        private const val CACHE_PREFIX = "ws:ticket"
    }

    override fun apply(config: AuthorizationConfig): GatewayFilter = GatewayFilter { exchange, chain ->
        val request = exchange.request
        log.info("header request = ${request.uri}")
        val isWebSocket = request.headers.getFirst("Upgrade")?.lowercase() == "websocket"

        if (!isWebSocket) throw GatewayException(GatewayExceptionCode.BAD_REQUEST)

        try {
            val ticket = request.cookies.getFirst(WS_TICKET_HEADER)?.value
                ?: return@GatewayFilter errorFilter.onError(
                    exchange,
                    GatewayException(GatewayExceptionCode.BAD_REQUEST)
                )

            val accountId = cachePort.get("$CACHE_PREFIX:$ticket", String::class.java) ?: return@GatewayFilter errorFilter.onError(
                exchange,
                GatewayException(GatewayExceptionCode.BAD_REQUEST)
            )

            val decorated = object : ServerHttpRequestDecorator(exchange.request) {
                override fun getHeaders(): HttpHeaders {
                    val headers = HttpHeaders()
                    headers.putAll(super.getHeaders())
                    headers.add(AccountHeaderName.ACCOUNT_ID, accountId)
                    return headers
                }
            }

            log.info("WebSocket connection authorized for accountId: $accountId")
            return@GatewayFilter chain.filter(exchange.mutate().request(decorated).build())

        } catch (e: JsonProcessingException) {
            return@GatewayFilter errorFilter.onError(exchange, e)
        } catch (e: GatewayException) {
            return@GatewayFilter errorFilter.onError(exchange, e)
        } catch (e: Exception) {
            log.error("Unexpected error during WebSocket authorization: ${e.message}")
            return@GatewayFilter errorFilter.onError(exchange, GatewayException(GatewayExceptionCode.BAD_REQUEST))
        }
    }
}