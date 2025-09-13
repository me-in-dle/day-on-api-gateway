package com.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.AntPathMatcher

@Configuration
class AntPathMatcherConfig {

    @Bean
    fun antPathMatcher(): AntPathMatcher {
        return AntPathMatcher()
    }
}