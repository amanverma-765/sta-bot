package com.ark.stabot.scraper

import co.touchlab.kermit.Logger
import com.ark.stabot.parser.PayloadParser
import com.ark.stabot.parser.TrademarkParser
import com.ark.stabot.utils.Constants.TRADEMARK_URL
import com.ark.stabot.utils.retryWithExponentialBackoff
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.springframework.stereotype.Component

@Component
class TrademarkScraper(
    private val payloadParser: PayloadParser,
    private val trademarkParser: TrademarkParser
) {

    // Retry parameters
    private val maxRetries = 50
    private val initialRetryDelayMs = 120000L // 2 minutes
    private val maxRetryDelayMs = 900000L    // 10 minutes

    suspend fun requestTrademarkData(
        httpClient: HttpClient,
        appId: String,
        captcha: String,
        defaultHeaders: Map<String, String>
    ): String? {

        val finalResponse = retryWithExponentialBackoff(maxRetries, initialRetryDelayMs, maxRetryDelayMs) {

            Logger.i("Extraction started for $appId")

            val firstPageResponse = httpClient.get(TRADEMARK_URL) {
                headers {
                    defaultHeaders.forEach { (key, value) ->
                        append(key, value)
                    }
                }
            }
            if (firstPageResponse.status != HttpStatusCode.OK) {
                if (
                    firstPageResponse.status == HttpStatusCode.InternalServerError
                    || firstPageResponse.status == HttpStatusCode.Unauthorized
                ) {
                    Logger.e("Blocked from server, with status code: ${firstPageResponse.status.value} ")
                    throw IllegalStateException("Blocked from server, with status code: ${firstPageResponse.status.value}")
                } else {
                    Logger.e("Failed to fetch Trademark, found status code: ${firstPageResponse.status.value} ")
                    throw RuntimeException("Failed to fetch Trademark, found status code: ${firstPageResponse.status.value}")
                }
            }
            val firstPageFormData = payloadParser.getPayloadFromFirstPage(firstPageResponse.bodyAsText())

            val secondPageResponse = httpClient.post(TRADEMARK_URL) {
                contentType(ContentType.MultiPart.FormData)
                setBody(firstPageFormData)
                headers { defaultHeaders.forEach { (key, value) -> append(key, value) } }
            }.bodyAsText()
            val secondPageFormData = payloadParser.getPayloadFromSecondPage(appId, captcha, secondPageResponse)

            val thirdPageResponse = httpClient.post(TRADEMARK_URL) {
                contentType(ContentType.MultiPart.FormData)
                setBody(secondPageFormData)
                headers { defaultHeaders.forEach { (key, value) -> append(key, value) } }
            }.bodyAsText()
            if (!trademarkParser.checkIfOnCorrectPage(thirdPageResponse)) {
                val errorMessage = "No Trademark found, Either Trademark id: $appId is invalid or doesn't exist"
                Logger.e(errorMessage)
                return@retryWithExponentialBackoff null
            }
            val finalFormData = payloadParser.getPayloadFromThirdPage(thirdPageResponse)

            httpClient.post(TRADEMARK_URL) {
                contentType(ContentType.MultiPart.FormData)
                setBody(finalFormData)
                headers { defaultHeaders.forEach { (key, value) -> append(key, value) } }
            }.bodyAsText()
        }
        Logger.i("Extraction completed for $appId")
        return finalResponse
    }

}
