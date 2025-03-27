package com.ark.stabot.scraper

import com.ark.stabot.utils.encodeTmNumber
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.headers
import io.ktor.client.statement.*
import io.ktor.http.*
import org.springframework.stereotype.Component


@Component
class OppositionScraper {
    suspend fun scrapeOpponentData(
        httpClient: HttpClient,
        defaultHeaders: Map<String, String>,
        oppNumber: String
    ): String? {
        val encodedOppNumber = encodeTmNumber(oppNumber)
        val baseUrl = "https://tmrsearch.ipindia.gov.in/eregister/ShowOppRecDetails.aspx?ID=$encodedOppNumber&typ=O"

        val response = httpClient.get(baseUrl) {
            headers { defaultHeaders.forEach { (key, value) -> append(key, value) } }
        }
        if (response.status == HttpStatusCode.OK) {
            return response.bodyAsText()
        }
        return null
    }
}
