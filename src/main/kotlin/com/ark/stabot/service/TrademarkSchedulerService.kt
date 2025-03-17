package com.ark.stabot.service

import co.touchlab.kermit.Logger
import com.ark.stabot.infrastructure.persistent.mapper.TrademarkMapper.toTrademarkEntity
import com.ark.stabot.infrastructure.persistent.repo.TrademarkRepository
import com.ark.stabot.model.Trademark
import com.ark.stabot.scraper.StaScraper
import com.ark.stabot.utils.Constants
import jakarta.transaction.Transactional
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicReference

@Service
@EnableScheduling
class TrademarkSchedulerService @Autowired constructor(
    private val staScraper: StaScraper,
    private val trademarkRepository: TrademarkRepository
) {

    private var currentApplicationNumber = AtomicReference<String>()
    private var isRunning = false
    private val chunkSize = 5000 // Number of trademarks to process per transaction
    private val collectedTrademarks = mutableListOf<Trademark>()

    @Scheduled(fixedRate = 3600000) // Run every hour
    @Transactional
    fun scheduleTrademarkScraping() {
        if (isRunning) {
            Logger.i("Scraping is already in progress")
            return
        }

        try {
            isRunning = true

            // Determine starting point
            val lastProcessedTrademark = trademarkRepository
                .findAll()
                .maxByOrNull { it.applicationNumber }
                ?.applicationNumber ?: Constants.INITIAL_TRADEMARK

            currentApplicationNumber.set(lastProcessedTrademark)
            Logger.i("Starting trademark scraping from application number: ${currentApplicationNumber.get()}")

            // Generate a list of subsequent application numbers
            val applicationNumbers = generateApplicationNumbers(currentApplicationNumber.get(), chunkSize)

            runBlocking {
                staScraper.scrapeTrademarkByList(
                    applicationNumbers = applicationNumbers,
                    threadCount = Constants.MAX_THREADS,
                    progressCallback = { progress, completed, total, current ->
                        Logger.i("Scraping progress: $progress, $completed/$total, current: $current")
                        current?.let { currentApplicationNumber.set(it) }
                    },
                    trademarkCallback = { trademark ->
                        trademark?.let {
                            collectedTrademarks.add(it)
                            if (collectedTrademarks.size >= chunkSize) {
                                saveTrademarks(collectedTrademarks)
                                collectedTrademarks.clear()
                            }
                        }
                    }
                )
            }

            // Save any remaining trademarks
            if (collectedTrademarks.isNotEmpty()) {
                saveTrademarks(collectedTrademarks)
                collectedTrademarks.clear()
            }

            Logger.i("Trademark scraping completed at ${LocalDateTime.now()}")
        } catch (e: Exception) {
            Logger.e("Error during scheduled trademark scraping", e)
        } finally {
            isRunning = false
        }
    }

    @Transactional
    fun saveTrademarks(trademarks: List<Trademark>) {
        Logger.i("Saving batch of ${trademarks.size} trademarks to database")
        val entities = trademarks.map { it.toTrademarkEntity() }
        trademarkRepository.saveAll(entities)
    }

   private fun generateApplicationNumbers(startingNumber: String, count: Int): List<String> {
       val numericValue = startingNumber.toIntOrNull() ?: return emptyList()
       return (numericValue until numericValue + count).map { it.toString() }
   }

}