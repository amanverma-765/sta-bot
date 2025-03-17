package com.ark.stabot.uptime

import com.ark.stabot.model.WebsiteState
import com.ark.stabot.model.WebsiteStatus
import com.ark.stabot.config.KtorClientFactory
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Component


@Component
class UptimeBot(ktorClientFactory: KtorClientFactory) {

    private val httpClient = ktorClientFactory.createHttpClient()

    fun monitorWebsite(
        url: String,
        intervalMs: Long = 10000,
        timeoutMs: Long = 10000,
        highLoadThresholdMs: Long = 3000
    ): Flow<WebsiteStatus> = flow {
        while (currentCoroutineContext().isActive) {
            val startTime = System.currentTimeMillis()
            val status = try {
                withTimeout(timeoutMs) {
                    val response = httpClient.get(url)
                    val responseTime = System.currentTimeMillis() - startTime

                    val state = when {
                        !response.status.isSuccess() -> WebsiteState.DOWN
                        responseTime > highLoadThresholdMs -> WebsiteState.HIGH_LOAD
                        else -> WebsiteState.UP
                    }

                    val errorMessage = when (state) {
                        WebsiteState.DOWN -> "HTTP Status: ${response.status.value}"
                        WebsiteState.HIGH_LOAD -> "High response time: ${responseTime}ms"
                        else -> null
                    }

                    WebsiteStatus(url, state, responseTime, errorMessage)
                }
            } catch (e: Exception) {
                WebsiteStatus(url, WebsiteState.DOWN, 0, e.message)
            }

            emit(status)
            delay(intervalMs)
        }
    }
}
