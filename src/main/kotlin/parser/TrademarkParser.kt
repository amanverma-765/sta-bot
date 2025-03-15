package com.ark.parser

import co.touchlab.kermit.Logger
import com.ark.model.Trademark
import org.jsoup.Jsoup
import kotlin.collections.set

class TrademarkParser {

    fun parseTrademarkDetails(response: String): Trademark? {
        var trademark: Trademark? = null

        try {
            val tableData = mutableMapOf<String, String>()
            val doc = Jsoup.parse(response)

            val tmDataPanel = doc.select("#panelgetdetail")

            // Extract key-value pairs from the main table
            val rows = tmDataPanel.select("table[border='1'] tr")
            if (rows.isEmpty()) {
                Logger.e("Invalid trademark found")
                throw IllegalArgumentException("Invalid trademark found")
            }
            for (row in rows) {
                val cells = row.select("td")
                if (cells.size == 2) {
                    val key = cells[0].text().trim()
                    var value = cells[1].html().trim()
                        .replace("\n", ", ")
                        .replace("<br>", " ")
                        .replace("&nbsp;", " ")
                    value = if (value.isEmpty() || value.isBlank()) "NA" else value
                    tableData[key] = value
                }
            }

            // Extract Status
            val statusElement = doc.select("td font:contains(Status)").first()?.nextElementSibling()
            val status = statusElement?.text()?.trim()
            tableData["Status"] = status ?: run {
                Logger.e("Trademark status not found")
                throw RuntimeException("Status not found for trademark: ${tableData["TM Application No."]}")
            }

            trademark = Trademark(
                applicationNumber = tableData["TM Application No."]
                    ?: throw RuntimeException("No Application Number found"),
                status = tableData["Status"] ?: throw RuntimeException("No Status found"),
                tmClass = tableData["Class"] ?: throw RuntimeException("No Class Found"),
                dateOfApplication = tableData["Date of Application"]
                    ?: throw RuntimeException("No Date of Application found"),
                appropriateOffice = tableData["Appropriate Office"],
                state = tableData["State"],
                country = tableData["Country"],
                filingMode = tableData["Filing Mode"],
                tmAppliedFor = tableData["TM Applied For"] ?: throw RuntimeException("No TM Applied For found"),
                tmCategory = tableData["TM Category"],
                tmType = tableData["Trade Mark Type"] ?: throw RuntimeException("No Trade Mark Type found"),
                userDetails = tableData["User Detail"],
                certDetail = tableData["Certificate Detail"],
                validUpTo = tableData["Valid upto/ Renewed upto"],
                proprietorName = tableData["Proprietor name"],
                proprietorAddress = tableData["Proprietor Address"],
                emailId = tableData["Email Id"],
                agentName = tableData["Attorney name"],
                agentAddress = tableData["Attorney Address"],
                publicationDetails = tableData["Publication Details"]
            )

        } catch (ex: Exception) {
            Logger.e("Error while parsing trademark table: ${ex.message}", throwable = ex)
        }
        return trademark
    }

    fun checkIfOnCorrectPage(response: String): Boolean {
        var correctPage = false
        try {
            val doc = Jsoup.parse(response)
            val headers = doc.select("#SearchWMDatagrid tr:first-child td")
            for (header in headers) {
                if (header.text().contains("Proprietor Name", ignoreCase = true)) {
                    correctPage = true
                }
            }
        } catch (ex: RuntimeException) {
            correctPage = false
        }
        return correctPage
    }

}