package com.ark.stabot.scraper

import co.touchlab.kermit.Logger
import com.ark.stabot.model.Trademark
import com.ark.stabot.parser.TrademarkParser
import com.ark.stabot.utils.Constants.MAX_THREADS
import com.ark.stabot.config.KtorClientFactory
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

            val trademarkResponse = trademarkScraper.requestTrademarkData(
                httpClient,
                applicationId,
                captchaCode,
                defaultHeaders
            ) ?: return createEmptyTrademark(applicationId)

            val trademark = trademarkParser.parseTrademarkDetails(trademarkResponse)
                ?: return createEmptyTrademark(applicationId)

            return trademark
        } catch (ex: Exception) {
            Logger.e("Error while scraping by application number: ${ex.message}", throwable = ex)
            throw ex
        }
    }


    suspend fun scrapeTrademarkByList(
        applicationNumbers: List<String>,
        threadCount: Int = MAX_THREADS,
        progressCallback: (
            progress: Float,
            completed: Int,
            total: Int,
            currentTm: String?
        ) -> Unit = { _, _, _, _ -> },
        trademarkCallback: (trademark: Trademark?) -> Unit = { _ -> }
    ) {
        if (applicationNumbers.isEmpty()) return

        Logger.i("Starting parallel scraping for ${applicationNumbers.size} trademarks using $threadCount threads")

        // Create chunks of application numbers distributed evenly across threads
        val chunkSize = (applicationNumbers.size + threadCount - 1) / threadCount
        val chunks = applicationNumbers.chunked(chunkSize)

        // Create a thread pool with a fixed number of threads
        val dispatcher = Executors.newFixedThreadPool(threadCount).asCoroutineDispatcher()

        try {
            var completedCount = 0
            val totalCount = applicationNumbers.size

            withContext(dispatcher) {
                chunks.map { chunk ->
                    async {
                        val httpClient = ktorClientFactory.createHttpClient()
                        httpClient.use { client ->
                            // get new header for each thread
                            val defaultHeaders = getDefaultHeaders()
                            // Get a fresh captcha for each thread
                            val captcha = captchaResolver.requestCaptcha(client, defaultHeaders)
                            chunk.forEach { appId ->
                                try {
                                    val trademark = scrapeTrademarkById(
                                        httpClient = httpClient,
                                        applicationId = appId,
                                        captcha = captcha,
                                        defaultHeaders = defaultHeaders
                                    )
                                    // Update progress after each successful scrape
                                    synchronized(this@StaScraper) {
                                        completedCount++
                                        val progress = completedCount.toFloat() / totalCount
                                        progressCallback(
                                            progress,
                                            completedCount,
                                            totalCount,
                                            trademark.applicationNumber
                                        )
                                        trademarkCallback(trademark)
                                    }
                                } catch (ex: Exception) {
                                    Logger.e("Error scraping trademark $appId: ${ex.message}")

                                    // Update progress even for failed scrapes
                                    synchronized(this@StaScraper) {
                                        completedCount++
                                        val progress = completedCount.toFloat() / totalCount
                                        progressCallback(progress, completedCount, totalCount, appId)
                                        trademarkCallback(null)
                                    }
                                }
                            }
                        }
                    }
                }.awaitAll()
            }
            Logger.i("Completed parallel scraping for $totalCount trademarks")
        } catch (ex: Exception) {
            Logger.e("Error during parallel trademark scraping: ${ex.message}", ex)
            throw ex
        } finally {
            dispatcher.close()
        }
    }
}