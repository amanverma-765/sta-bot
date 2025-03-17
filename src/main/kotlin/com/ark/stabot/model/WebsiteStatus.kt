package com.ark.stabot.model

data class WebsiteStatus(
    val url: String,
    val state: WebsiteState,
    val responseTimeMs: Long = 0,
    val errorMessage: String? = null
)