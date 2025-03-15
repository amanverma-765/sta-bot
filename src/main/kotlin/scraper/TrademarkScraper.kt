package com.ark.scraper

import co.touchlab.kermit.Logger
import com.ark.parser.PayloadParser
import com.ark.parser.TrademarkParser
import com.ark.utils.Constants.TRADEMARK_URL
import com.ark.utils.createEmptyTrademark
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay

class TrademarkScraper(
    private val payloadParser: PayloadParser = PayloadParser(),
    private val trademarkParser: TrademarkParser = TrademarkParser()
) {

    // Retry parameters
    private val maxRetries = 5
    private val retryDelay = 50000L

    suspend fun requestTrademarkData(
        httpClient: HttpClient,
        appId: String,
        captcha: String
    ): String? {

        val finalResponse = retry(maxRetries, retryDelay) {

            Logger.i("Extraction started for $appId")

            val firstPageResponse = httpClient.get(TRADEMARK_URL)
            if (firstPageResponse.status != HttpStatusCode.OK) {
                if (
                    firstPageResponse.status == HttpStatusCode.InternalServerError
                    || firstPageResponse.status == HttpStatusCode.Unauthorized
                ) {
                    Logger.e("Blocked from server with status code: ${firstPageResponse.status.value} ")
                    throw IllegalStateException("Blocked from server with status code: ${firstPageResponse.status.value}")
                } else {
                    Logger.e("Failed to fetch Trademark, found status code: ${firstPageResponse.status.value} ")
                    throw RuntimeException("Failed to fetch Trademark, found status code: ${firstPageResponse.status.value}")
                }
            }
            val firstPageFormData = payloadParser.getPayloadFromFirstPage(firstPageResponse.bodyAsText())

            val secondPageResponse = httpClient.post(TRADEMARK_URL) {
                contentType(ContentType.MultiPart.FormData)
                setBody(firstPageFormData)
            }.bodyAsText()
            val secondPageFormData = payloadParser.getPayloadFromSecondPage(appId, captcha, secondPageResponse)

            val thirdPageResponse = httpClient.post(TRADEMARK_URL) {
                contentType(ContentType.MultiPart.FormData)
                setBody(secondPageFormData)
            }.bodyAsText()
            if (!trademarkParser.checkIfOnCorrectPage(thirdPageResponse)) {
                val errorMessage = "No Trademark found, Either Trademark id: $appId is invalid or doesn't exist"
                Logger.e(errorMessage)
                return@retry null
            }
            val finalFormData = payloadParser.getPayloadFromThirdPage(thirdPageResponse)

            httpClient.post(TRADEMARK_URL) {
                contentType(ContentType.MultiPart.FormData)
                setBody(finalFormData)
            }.bodyAsText()
        }
        Logger.i("Extraction completed for $appId")
        return finalResponse
    }

    // Retry function
    private suspend fun <T> retry(maxRetries: Int, delayMillis: Long, block: suspend () -> T): T {
        var currentAttempt = 0
        var lastError: Throwable? = null
        while (currentAttempt < maxRetries) {
            try {
                return block()
            } catch (ex: Exception) {
                currentAttempt++
                lastError = ex
                Logger.e("Attempt $currentAttempt failed: ${ex.message}. Retrying in $delayMillis ms...")
                delay(delayMillis)
            }
        }
        throw lastError ?: IllegalStateException("Unknown error during retry")
    }
}