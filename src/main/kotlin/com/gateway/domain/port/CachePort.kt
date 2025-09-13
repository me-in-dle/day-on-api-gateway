package com.gateway.domain.port

interface CachePort {
    fun <T :Any> put(key: String, value: T, ttlMillis: Long)
    fun <T : Any> putIfAbsent(key: String, value: T, ttlMillis: Long): Boolean?
    fun <T : Any> get(key: String, type: Class<T>): T?
    fun delete(key: String)
}