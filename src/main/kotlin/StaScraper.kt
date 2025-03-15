package com.ark

import co.touchlab.kermit.Logger
import com.ark.model.Trademark
import com.ark.parser.TrademarkParser
import com.ark.scraper.CaptchaResolver
import com.ark.scraper.TrademarkScraper
import com.ark.utils.KtorClientFactory
import com.ark.utils.createEmptyTrademark
import io.ktor.client.*
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class StaScraper(
    private val ktorClientFactory: KtorClientFactory = KtorClientFactory(),
    private val captchaResolver: CaptchaResolver = CaptchaResolver(),
    private val trademarkScraper: TrademarkScraper = TrademarkScraper(),
    private val trademarkParser: TrademarkParser = TrademarkParser()
) {

    private suspend fun scrapeTrademarkById(
        applicationId: String,
        httpClient: HttpClient,
        captcha: String? = null
    ): Trademark {
        try {
            val captchaCode = captcha ?: captchaResolver.requestCaptcha(httpClient)

            val trademarkResponse = trademarkScraper.requestTrademarkData(
                httpClient,
                applicationId,
                captchaCode!!
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
        threadCount: Int = 25,
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
        println(chunks[0].size)

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
                            // Get a fresh captcha for each thread
                            val captcha = captchaResolver.requestCaptcha(client)

                            chunk.forEach { appId ->
                                try {
                                    val trademark = scrapeTrademarkById(
                                        httpClient = httpClient,
                                        applicationId = appId,
                                        captcha = captcha
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
                                } catch (e: Exception) {
                                    Logger.e("Error scraping trademark $appId: ${e.message}")

                                    // Update progress even for failed scrapes
                                    synchronized(this@StaScraper) {
                                        completedCount++
                                        val progress = completedCount.toFloat() / totalCount
                                        progressCallback(progress, completedCount, totalCount, null)
                                        trademarkCallback(null)
                                    }
                                }
                            }
                        }
                    }
                }.awaitAll()
            }
            Logger.i("Completed parallel scraping for $totalCount trademarks")
        } catch (e: Exception) {
            Logger.e("Error during parallel trademark scraping: ${e.message}", e)
            throw e
        } finally {
            dispatcher.close()
        }
    }
}