package com.ark

import kotlinx.serialization.json.Json

suspend fun main() {

    val startTime = System.currentTimeMillis()
    val staScraper = StaScraper()

    // create a list of 100 trademarks to scrape
    val applicationList = (6000000..6000100).map { it.toString() }
    val tmList = staScraper.scrapeTrademarkByList(applicationList)

    val endTime = System.currentTimeMillis()
    val elapsedTime = endTime - startTime

    println("Scraped ${tmList.size} trademarks in ${elapsedTime / 1000.0} seconds")
    println(Json.encodeToString(tmList))
}