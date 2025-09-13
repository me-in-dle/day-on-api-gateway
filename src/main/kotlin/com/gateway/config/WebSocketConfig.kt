package com.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
class WebSocketConfig {

    @Bean
    fun handlerMapping(webSocketHandler: WebSocketHandler): HandlerMapping {
        val urlMap = mapOf("/ws" to webSocketHandler)
        return SimpleUrlHandlerMapping().apply {
            this.urlMap = urlMap
            this.order = -1
        }
    }

    @Bean
    fun handlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }
}