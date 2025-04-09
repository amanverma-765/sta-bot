package com.ark.stabot.utils

object Constants {
    const val CAPTCHA_URL = "https://tmrsearch.ipindia.gov.in/eregister/captcha.ashx"
    const val TRADEMARK_URL = "https://tmrsearch.ipindia.gov.in/eregister/Application_View.aspx"
    const val GET_CAPTCHA_URL = "https://tmrsearch.ipindia.gov.in/eregister/Viewdetails_Copyright.aspx/GetCaptcha"
    const val MAX_THREADS = 25 // number of threads to run scraper on
    const val INITIAL_TRADEMARK = "478006" // starting trademark
    const val TRADEMARK_CHUNK_SIZE = 5000 // trademarks to scrape on each run
    const val NA_THRESHOLD = 2500 // Trademarks to be null to stop the scraper
    const val TASK_FREQ = 600000L // Task frequency (Every 10 Mins)
    const val MAX_APPLICATION_NUMBER = 478016 // Maximum application number to scrape
}
