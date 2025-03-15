package com.ark.model

sealed interface ScraperState {
    data object Running: ScraperState
    data object IDLE: ScraperState
    data class Error(val message: String): ScraperState
}