package com.ark.stabot.config

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Configuration


@Configuration
class KtorClientFactory {
    fun createHttpClient(
        ignoreUnknownKeys: Boolean = true,
        isLenient: Boolean = true
    ): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(false)
                }
            }
            install (HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        this.ignoreUnknownKeys = ignoreUnknownKeys
                        this.isLenient = isLenient
                    }
                )
            }
            install(HttpTimeout)
            {
                requestTimeoutMillis = 20000
                connectTimeoutMillis = 20000
                socketTimeoutMillis = 20000
            }
        }
    }
}
