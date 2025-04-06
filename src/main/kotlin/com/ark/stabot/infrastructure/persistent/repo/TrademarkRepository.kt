package com.ark.stabot.infrastructure.persistent.repo

import com.ark.stabot.infrastructure.persistent.entity.TrademarkEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TrademarkRepository: JpaRepository<TrademarkEntity, Long> {
    @Query("SELECT t FROM TrademarkEntity t ORDER BY CAST(t.applicationNumber AS long) DESC")
    fun findTopByApplicationNumberAsNumberDesc(): TrademarkEntity?
}