package com.gateway.api.config

import com.gateway.api.filter.AuthorizationHeaderFilter
import com.gateway.api.filter.RefreshTokenHeaderFilter
import com.gateway.api.util.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus

@Configuration
class FilterConfig(
    private val authorizationConfig: AuthorizationConfig,
) {
    @Value("\${gateway.server.main.uri}")
    lateinit var mainServerUri: String

    @Bean
    fun blockInternalRequestFilter(): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            log.info("uri invalid = ${exchange.request.uri}")
            exchange.response.statusCode = HttpStatus.FORBIDDEN
            exchange.response.setComplete()
        }
    }

    @Bean
    fun setRoutes(
        builder: RouteLocatorBuilder,
        authorizationHeaderFilter: AuthorizationHeaderFilter,
        refreshTokenHeaderFilter: RefreshTokenHeaderFilter,
    ): RouteLocator {

        return builder.routes()
            .route("block-internal") { route ->
                route.path(
                    "/api/internal/**",
                )
                    .filters { spec -> spec.filter(blockInternalRequestFilter()) }
                    .uri("forward:/invalid")
            }
            .route("general") { route ->
                route.path("/api/**")
                    .filters { spec -> spec.filter(authorizationHeaderFilter.apply(authorizationConfig)) }
                    .uri(mainServerUri)

            }
            .build()
    }

    companion object : Logger()
}