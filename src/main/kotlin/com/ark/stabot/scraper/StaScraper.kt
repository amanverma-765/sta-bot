package com.ark.stabot.scraper

import co.touchlab.kermit.Logger
import com.ark.stabot.model.Trademark
import com.ark.stabot.config.KtorClientFactory
import com.ark.stabot.parser.TrademarkParser
import com.ark.stabot.utils.Constants
import com.ark.stabot.utils.Header.getDefaultHeaders
import com.ark.stabot.utils.createEmptyTrademark
import io.ktor.client.*
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class StaScraper(
    private val ktorClientFactory: KtorClientFactory,
    private val captchaResolver: CaptchaResolver,
    private val trademarkScraper: TrademarkScraper,
    private val trademarkParser: TrademarkParser
) {

    private suspend fun scrapeTrademarkById(
        applicationId: String,
        httpClient: HttpClient,
        defaultHeaders: Map<String, String>,
        captcha: String? = null
    ): Trademark {
        try {
            val captchaCode = captcha ?: captchaResolver.requestCaptcha(httpClient, defaultHeaders)

            val trademarkResponse = trademarkScraper.scrapeTrademarkData(
                httpClient = httpClient,
                appId = applicationId,
                captcha = captchaCode,
                defaultHeaders = defaultHeaders
            ) ?: return createEmptyTrademark(applicationId)

            val trademark = trademarkParser.parseTrademarkDetails(
                httpClient = httpClient,
                defaultHeaders = defaultHeaders,
                response = trademarkResponse
            ) ?: return createEmptyTrademark(applicationId, parsingError = true)

            return trademark
        } catch (ex: Exception) {
            Logger.e("Error while scraping by application number: ${ex.message}", throwable = ex)
            throw ex
        }
    }


suspend fun scrapeTrademarkByList(
    applicationNumbers: List<String>,
    threadCount: Int = Constants.MAX_THREADS,
): List<Trademark> {
    if (applicationNumbers.isEmpty()) return emptyList()

    Logger.i("Starting parallel scraping for ${applicationNumbers.size} trademarks using $threadCount threads")

    // Create chunks of application numbers distributed evenly across threads
    val chunkSize = (applicationNumbers.size + threadCount - 1) / threadCount
    val chunks = applicationNumbers.chunked(chunkSize)

    // Create a thread pool with a fixed number of threads
    val dispatcher = Executors.newFixedThreadPool(threadCount).asCoroutineDispatcher()

    return try {
        val results = withContext(dispatcher) {
            chunks.map { chunk ->
                async {
                    val httpClient = ktorClientFactory.createHttpClient()
                    val defaultHeaders = getDefaultHeaders()
                    httpClient.use { client ->
                        // Get a fresh captcha for each thread
                        val captcha = captchaResolver.requestCaptcha(client, defaultHeaders)

                        chunk.mapNotNull { appId ->
                            try {
                                val trademark = scrapeTrademarkById(
                                    httpClient = client,
                                    applicationId = appId,
                                    captcha = captcha,
                                    defaultHeaders = defaultHeaders
                                )
                                trademark
                            } catch (e: Exception) {
                                Logger.e("Error scraping trademark $appId: ${e.message}")
                                null
                            }
                        }
                    }
                }
            }.awaitAll().flatten()
        }

        Logger.i("Completed parallel scraping with ${results.size} trademarks collected")
        results
    } catch (e: Exception) {
        Logger.e("Error during parallel trademark scraping: ${e.message}", e)
        emptyList()
    } finally {
        dispatcher.close()
    }
}
}