package com.ark.stabot.scraper

import co.touchlab.kermit.Logger
import com.ark.stabot.utils.Constants.CAPTCHA_URL
import com.ark.stabot.utils.Constants.GET_CAPTCHA_URL
import com.ark.stabot.utils.Constants.TRADEMARK_URL
import com.ark.stabot.utils.retryWithExponentialBackoff
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Component

@Component
class CaptchaResolver {

    // Retry parameters
    private val maxRetries = 50
    private val initialRetryDelayMs = 120000L // 2 minutes
    private val maxRetryDelayMs = 900000L    // 15 minutes

    suspend fun requestCaptcha(httpClient: HttpClient, defaultHeaders: Map<String, String>): String {
        val payload = "{}"
        return retryWithExponentialBackoff(maxRetries, initialRetryDelayMs, maxRetryDelayMs) {

            Logger.i("Fetching new CAPTCHA...")
            httpClient.get(CAPTCHA_URL) { headers { defaultHeaders.forEach { (key, value) -> append(key, value) } } }
            httpClient.get(TRADEMARK_URL) { headers { defaultHeaders.forEach { (key, value) -> append(key, value) } } }

            val response = httpClient.post(GET_CAPTCHA_URL) {
                contentType(ContentType.Application.Json)
                headers { defaultHeaders.forEach { (key, value) -> append(key, value) } }
                setBody(payload)
            }

            if (response.status == HttpStatusCode.OK) {
                val jsonResponse: CaptchaResponse = response.body()
                Logger.i("Captcha Request was successful! $jsonResponse")
                jsonResponse.d
            } else {
                Logger.e("Failed with status code: ${response.status.value}")
                Logger.e("Response text: ${response.bodyAsText()}")
                throw IllegalStateException("Failed with status code: ${response.status.value}")
            }
        }
    }

    @Serializable
    data class CaptchaResponse(val d: String)
}