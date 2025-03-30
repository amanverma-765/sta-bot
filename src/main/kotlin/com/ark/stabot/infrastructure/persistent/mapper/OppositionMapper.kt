package com.ark.stabot.infrastructure.persistent.mapper

import com.ark.stabot.infrastructure.persistent.entity.OppositionEntity
import com.ark.stabot.model.Opposition

object OppositionMapper {
    fun OppositionEntity.toOpposition(): Opposition {
        return Opposition(
            oppositionNumber = this.oppositionNumber,
            oppositionDate = this.oppositionDate,
            opponentName = this.opponentName,
            opponentCode = this.opponentCode,
            opponentAddr = this.opponentAddr,
            agentName = this.agentName,
            agentAddr = this.agentAddr,
            status = this.status,
            decision = this.decision,
            trademarkRef = this.trademarkRef
        )
    }

    fun Opposition.toOppositionEntity(): OppositionEntity {
        return OppositionEntity(
            oppositionNumber = this.oppositionNumber,
            oppositionDate = this.oppositionDate,
            opponentName = this.opponentName,
            opponentCode = this.opponentCode,
            opponentAddr = this.opponentAddr,
            agentName = this.agentName,
            agentAddr = this.agentAddr,
            status = this.status,
            decision = this.decision,
            trademarkRef = this.trademarkRef
        )
    }
}