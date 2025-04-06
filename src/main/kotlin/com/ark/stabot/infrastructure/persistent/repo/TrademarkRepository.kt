package com.ark.stabot.infrastructure.persistent.repo

import com.ark.stabot.infrastructure.persistent.entity.TrademarkEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TrademarkRepository: JpaRepository<TrademarkEntity, Long> {
    fun findTopByOrderByIdDesc(): TrademarkEntity?
}