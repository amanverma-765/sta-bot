package com.ark.stabot.model

import kotlinx.serialization.Serializable

@Serializable
data class Opposition(
    val oppositionNumber: String,
    val oppositionDate: String?,
    val opponentName: String?,
    val opponentCode: String?,
    val opponentAddr: String?,
    val agentName: String?,
    val agentAddr: String?,
    val status: String?,
    val decision: String?,
    val trademarkRef: String?
)