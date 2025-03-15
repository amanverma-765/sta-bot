package com.ark

import co.touchlab.kermit.Logger
import com.ark.model.Trademark
import kotlinx.serialization.json.Json

suspend fun main() {

    val startTime = System.currentTimeMillis()
    val staScraper = StaScraper()

    val tmList = mutableListOf<Trademark>()

    // create a list of 100 trademarks to scrape
    val applicationList = (6000000..(6000000 + 100)).map { it.toString() }
    staScraper.scrapeTrademarkByList(
        threadCount = 10,
        applicationNumbers = applicationList,
        progressCallback = { progress, completed, total, currentTm ->
            val percent = (progress * 100).toInt()
            Logger.d("Progress: $percent% | Completed: $completed of $total | Current: $currentTm")
        }
    ) {
        it?.let { tmList.add(it) } ?: throw NullPointerException()
    }

    val endTime = System.currentTimeMillis()
    val elapsedTime = endTime - startTime

    println("Scraped ${tmList.size} trademarks in ${elapsedTime / 1000.0} seconds")
    println(Json.encodeToString(tmList))
}