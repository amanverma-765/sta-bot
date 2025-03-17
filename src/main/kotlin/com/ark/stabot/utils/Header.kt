package com.ark.stabot.utils

object Header {
    private val userAgents = listOf(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Safari/605.1.15",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:127.0) Gecko/20100101 Firefox/127.0",
        "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:126.0) Gecko/20100101 Firefox/126.0",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36 Edg/134.0.0.0",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36"
    )

    private val chromiumVersions = listOf(
        "\"Chromium\";v=\"134\"", "\"Chromium\";v=\"133\"", "\"Chromium\";v=\"132\""
    )

    private val browsers = listOf(
        "\"Not-A.Brand\";v=\"99\"", "\"Google Chrome\";v=\"134\"", "\"Brave\";v=\"134\"", "\"Edg\";v=\"134\""
    )

    fun getDefaultHeaders(): Map<String, String> {
        val randomUserAgent = userAgents.random()
        val randomChromiumVersion = chromiumVersions.random()
        val randomBrowser = browsers.random()

        return mapOf(
            "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8",
            "Accept-Language" to "en-US,en;q=0.9",
            "Cache-Control" to "max-age=0",
            "Connection" to "keep-alive",
            "Origin" to "https://tmrsearch.ipindia.gov.in",
            "Referer" to "https://tmrsearch.ipindia.gov.in/eregister/Application_View.aspx",
            "Sec-Fetch-Dest" to "document",
            "Sec-Fetch-Mode" to "navigate",
            "Sec-Fetch-Site" to "same-origin",
            "Sec-Fetch-User" to "?1",
            "Sec-GPC" to (0..1).random().toString(),
            "Upgrade-Insecure-Requests" to "1",
            "User-Agent" to randomUserAgent,
            "sec-ch-ua" to "$randomChromiumVersion, $randomBrowser, \"Not:A-Brand\";v=\"24\"",
            "sec-ch-ua-mobile" to "?0",
            "sec-ch-ua-platform" to listOf("\"Windows\"", "\"Linux\"", "\"macOS\"").random()
        )
    }
}