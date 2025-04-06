package com.ark.stabot.controller

import com.ark.stabot.infrastructure.persistent.mapper.TrademarkMapper.toTrademark
import com.ark.stabot.infrastructure.persistent.repo.TrademarkRepository
import com.ark.stabot.utils.getTrademarkStatusTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class TrademarkController(private val trademarkRepository: TrademarkRepository) {

    @GetMapping("/")
    fun getTrademarkStatus(): String {
        val totalTrademarks = trademarkRepository.count()
        val lastTrademark = trademarkRepository.findTopByApplicationNumberAsNumberDesc()?.toTrademark()
        return getTrademarkStatusTemplate(totalTrademarks.toInt(), lastTrademark)
    }
}