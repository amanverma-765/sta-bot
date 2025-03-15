package com.ark.parser

import co.touchlab.kermit.Logger
import io.ktor.client.request.forms.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class PayloadParser {

    fun getPayloadFromThirdPage(response: String): MultiPartFormDataContent {
        Logger.i("Parsing third page payload")
        val doc = parseHtmlOrThrow(response)

        val formFields = mapOf(
            "ToolkitScriptManager1_HiddenField" to doc.getElementById("ToolkitScriptManager1_HiddenField")?.attr("value").orEmpty(),
            "__EVENTTARGET" to "SearchWMDatagrid\$ctl03\$lnkbtnappNumber1",
            "__EVENTARGUMENT" to doc.getElementById("__EVENTARGUMENT")?.attr("value").orEmpty(),
            "__VIEWSTATE" to doc.getElementById("__VIEWSTATE")?.attr("value").orEmpty(),
            "__VIEWSTATEGENERATOR" to doc.getElementById("__VIEWSTATEGENERATOR")?.attr("value").orEmpty(),
            "__VIEWSTATEENCRYPTED" to doc.getElementById("__VIEWSTATEENCRYPTED")?.attr("value").orEmpty(),
            "__EVENTVALIDATION" to doc.getElementById("__EVENTVALIDATION")?.attr("value").orEmpty()
        )

        return createMultiPartFormData(formFields)
    }

    fun getPayloadFromSecondPage(appId: String, captcha: String, response: String): MultiPartFormDataContent {
        Logger.i("Parsing second page payload")
        val doc = parseHtmlOrThrow(response)

        val formFields = mapOf(
            "ToolkitScriptManager1_HiddenField" to ";;AjaxControlToolkit, Version=3.5.11119.20050, Culture=neutral, " +
                    "PublicKeyToken=28f01b0e84b6d53e:en-US:8e147239-dd05-47b0-8fb3-f743a139f982:865923e8:91bd373d:" +
                    "8e72a662:411fea1c:acd642d2:596d588c:77c58d20:14b56adc:269a19ae",
            "__EVENTTARGET" to "",
            "__EVENTARGUMENT" to "",
            "__VIEWSTATE" to doc.getElementById("__VIEWSTATE")?.attr("value").orEmpty(),
            "__VIEWSTATEGENERATOR" to doc.getElementById("__VIEWSTATEGENERATOR")?.attr("value").orEmpty(),
            "__VIEWSTATEENCRYPTED" to "",
            "__EVENTVALIDATION" to doc.getElementById("__EVENTVALIDATION")?.attr("value").orEmpty(),
            "applNumber" to appId,
            "captcha1" to captcha,
            "btnView" to "View "
        )

        return createMultiPartFormData(formFields)
    }

    fun getPayloadFromFirstPage(response: String): MultiPartFormDataContent {
        Logger.i("Parsing first page payload")
        val doc = parseHtmlOrThrow(response)

        val formFields = mapOf(
            "ToolkitScriptManager1_HiddenField" to ";;AjaxControlToolkit, Version=3.5.11119.20050, Culture=neutral, " +
                    "PublicKeyToken=28f01b0e84b6d53e:en-US:8e147239-dd05-47b0-8fb3-f743a139f982:865923e8:91bd373d:" +
                    "8e72a662:411fea1c:acd642d2:596d588c:77c58d20:14b56adc:269a19ae",
            "__EVENTTARGET" to "rdb\$0",
            "__EVENTARGUMENT" to "",
            "__LASTFOCUS" to "",
            "__VIEWSTATE" to doc.getElementById("__VIEWSTATE")?.attr("value").orEmpty(),
            "__VIEWSTATEGENERATOR" to doc.getElementById("__VIEWSTATEGENERATOR")?.attr("value").orEmpty(),
            "__VIEWSTATEENCRYPTED" to "",
            "__EVENTVALIDATION" to doc.getElementById("__EVENTVALIDATION")?.attr("value").orEmpty(),
            "rdb" to "N"
        )

        return createMultiPartFormData(formFields)
    }

    private fun parseHtmlOrThrow(response: String): Document {
        return try {
            Jsoup.parse(response)
        } catch (e: Exception) {
            Logger.e("Failed to parse HTML response: ${e.message}")
            throw IllegalArgumentException("Could not parse HTML response", e)
        }
    }

    private fun createMultiPartFormData(fields: Map<String, String>): MultiPartFormDataContent {
        return try {
            MultiPartFormDataContent(
                formData {
                    fields.forEach { (key, value) ->
                        append(key, value)
                    }
                }
            )
        } catch (e: Exception) {
            Logger.e("Failed to create form data: ${e.message}", throwable = e)
            throw IllegalStateException("Error creating form data content", e)
        }
    }

}