package com.gateway.api.handler

import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class WebSocketCustomHandler : WebSocketHandler {
    override fun handle(session: WebSocketSession): Mono<Void> {
        return session.send(
            session.receive().map { it.payloadAsText }.map { session.textMessage("Echo: $it") }
        )
    }
}