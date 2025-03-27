package com.ark.stabot.parser

import co.touchlab.kermit.Logger
import com.ark.stabot.model.Opposition
import org.jsoup.Jsoup
import org.springframework.stereotype.Component

@Component
class OppositionParser {
    fun parseOpposition(response: String): Opposition? {
        try {
            val tableData = mutableMapOf<String, String>()
            val doc = Jsoup.parse(response)

            val oppositionPanel = doc.select("#panelgetdetail")

            // Extract key-value pairs from the main table
            val rows = oppositionPanel.select("table[border='1'] tr")
            if (rows.isEmpty()) {
                throw IllegalArgumentException("Opposition table not found")
            }

            for (row in rows) {
                val cells = row.select("td")
                if (cells.size == 2) {
                    val key = cells[0].text().trim()
                    var value = cells[1].text().trim()
                        .replace("\n", ", ")
                        .replace("<br>", " ")
                        .replace("&nbsp;", " ")
                    value = if (value.isEmpty() || value.isBlank()) "NA" else value
                    tableData[key] = value
                }
            }

            return Opposition(
                oppositionNumber = tableData["Opp/Rec Date"] ?: run {
                    throw RuntimeException("Opposition number not found")
                },
                oppositionDate = tableData["Opp/Rec Date"] ?: "NA",
                opponentCode = tableData["Opponent Code"] ?: "NA",
                opponentName = tableData["Opponent Name"] ?: "NA",
                opponentAddr = tableData["Opponent Address"] ?: "NA",
                agentName = tableData["Agent/Attorney Name"] ?: "NA",
                agentAddr = tableData["Agent/Attorney Address"] ?: "NA",
                status = tableData["Status"] ?: "NA",
                decision = tableData["Decision"] ?: "NA"
            )

        } catch (ex: Exception) {
            Logger.e("Error while parsing opposition table: ${ex.message}", ex)
            return null
        }
    }
}