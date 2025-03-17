package com.ark.stabot.utils

import co.touchlab.kermit.Logger
import kotlinx.coroutines.delay

// Exponential backoff retry function
suspend fun <T> retryWithExponentialBackoff(
    maxRetries: Int,
    initialDelayMs: Long,
    maxRetryDelayMs: Long,
    block: suspend () -> T
): T {
    var currentAttempt = 0
    var lastError: Throwable? = null

    while (currentAttempt < maxRetries) {
        try {
            return block()
        } catch (ex: Exception) {
            currentAttempt++
            lastError = ex

            if (currentAttempt >= maxRetries) break

            // Calculate exponential backoff with jitter
            val exponentialDelay = initialDelayMs * (1 shl (currentAttempt - 1))
            val jitter = (Math.random() * 0.3 * exponentialDelay).toLong()
            val delayWithJitter = (exponentialDelay + jitter).coerceAtMost(maxRetryDelayMs)

            Logger.e("Attempt $currentAttempt failed: ${ex.message}. Retrying in ${delayWithJitter}ms...")
            delay(delayWithJitter)
        }
    }
    throw lastError ?: IllegalStateException("Unknown error during retry")
}
