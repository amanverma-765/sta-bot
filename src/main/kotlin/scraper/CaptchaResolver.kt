package com.ark.scraper

import com.ark.utils.Constants.CAPTCHA_URL
import com.ark.utils.Constants.GET_CAPTCHA_URL
import com.ark.utils.Constants.TRADEMARK_URL
import com.ark.utils.Header.getDefaultHeaders
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

class CaptchaResolver {
    suspend fun requestCaptcha(httpClient: HttpClient): String? {
        val payload = "{}"
        return try {

            println("Fetching new CAPTCHA...")
            httpClient.get(CAPTCHA_URL) { headers { getDefaultHeaders() } }
            httpClient.get(TRADEMARK_URL) { headers { getDefaultHeaders() } }

            val response = httpClient.post(GET_CAPTCHA_URL) {
                contentType(ContentType.Application.Json)
                headers { getDefaultHeaders() }
                setBody(payload)
            }

            if (response.status == HttpStatusCode.OK) {
                val jsonResponse: CaptchaResponse = response.body()
                println("Captcha Request was successful! $jsonResponse")
                jsonResponse.d
            } else {
                println("Failed with status code: ${response.status.value}")
                println("Response text: ${response.bodyAsText()}")
                null
            }
        } catch (ex: Exception) {
            println("Exception occurred while requesting captcha: ${ex.message} \n $ex")
            return null
        }
    }

    @Serializable
    data class CaptchaResponse(val d: String)
}