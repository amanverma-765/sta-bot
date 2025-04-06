package com.ark.stabot.service

import co.touchlab.kermit.Logger
import com.ark.stabot.infrastructure.persistent.mapper.TrademarkMapper.toTrademarkEntity
import com.ark.stabot.infrastructure.persistent.repo.TrademarkRepository
import com.ark.stabot.model.Trademark
import com.ark.stabot.scraper.StaScraper
import com.ark.stabot.utils.Constants
import jakarta.transaction.Transactional
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@Service
@EnableScheduling
class TrademarkSchedulerService(
    private val staScraper: StaScraper,
    private val trademarkRepository: TrademarkRepository
) {

    private var currentApplicationNumber = AtomicReference<Int>()
    private var isRunning = false
    private val tmChunkSize = Constants.TRADEMARK_CHUNK_SIZE
    private val consecutiveNaCount = AtomicInteger(0)
    private val naThreshold = Constants.NA_THRESHOLD


    @Scheduled(fixedRate = Constants.TASK_FREQ)
    @Transactional
    fun scheduleTrademarkScraping() {
        if (isRunning) {
            Logger.i("Scraping is already in progress")
            return
        }

        try {
            isRunning = true
            consecutiveNaCount.set(0)

            // Determine starting point
            val lastProcessedTrademark = trademarkRepository
                .findTopByOrderByIdDesc()
                ?.applicationNumber?.toIntOrNull() ?: (Constants.INITIAL_TRADEMARK.toInt() - 1)

            currentApplicationNumber.set(lastProcessedTrademark + 1)

//            currentApplicationNumber.set(lastProcessedTrademark)
            Logger.i("Starting trademark scraping from application number: ${currentApplicationNumber.get()}")

            // Generate a list of subsequent application numbers
            val applicationNumbers = generateApplicationNumbers(currentApplicationNumber.get(), tmChunkSize)

            runBlocking {
                // Get scraper results
                val trademarks = staScraper.scrapeTrademarkByList(
                    applicationNumbers = applicationNumbers,
                    threadCount = Constants.MAX_THREADS
                )

                Logger.i("Retrieved ${trademarks.size} trademarks, preparing to save to database")

                // We'll save all trademarks unless we reach the threshold
                val allTrademarks = mutableListOf<Trademark>()
                var shouldStop = false

                // Check for consecutive NA trademarks
                var consecutiveNACount = 0
                for (trademark in trademarks) {
                    currentApplicationNumber.set(trademark.applicationNumber.toIntOrNull())
                    allTrademarks.add(trademark)

                    if (isEmptyTrademark(trademark)) {
                        consecutiveNACount++
                        Logger.i("Found NA trademark: ${trademark.applicationNumber}, consecutive NA count: $consecutiveNACount")

                        if (consecutiveNACount >= naThreshold) {
                            Logger.i("Reached $naThreshold consecutive NA trademarks. Stopping processing.")
                            shouldStop = true
                            break
                        }
                    } else {
                        consecutiveNACount = 0
                    }
                }

                // Save all trademarks collected so far if we haven't reached the NA threshold or have some to save
                if (!shouldStop && allTrademarks.isNotEmpty()) {
                    saveTrademarks(allTrademarks)
                    Logger.i("Saved ${allTrademarks.size} trademarks to database")
                }
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
        val sortedTrademarks = trademarks.sortedBy { it.applicationNumber }
        Logger.i("Saving ${sortedTrademarks.size} trademarks to database")
        val entities = sortedTrademarks.map { it.toTrademarkEntity() }
        trademarkRepository.saveAll(entities)
    }

    private fun generateApplicationNumbers(startingNumber: Int, count: Int): List<String> {
        return (startingNumber until startingNumber + count).map { it.toString() }
    }

    private fun isEmptyTrademark(trademark: Trademark): Boolean {
        // Check if all fields except applicationNumber are "NA"
        return trademark.status == "NA" &&
                trademark.tmClass == "NA" &&
                trademark.dateOfApplication == "NA" &&
                trademark.tmAppliedFor == "NA" &&
                trademark.tmType == "NA"
    }
}