package com.ark.stabot.infrastructure.persistent.mapper

import com.ark.stabot.infrastructure.persistent.entity.TrademarkEntity
import com.ark.stabot.infrastructure.persistent.mapper.OppositionMapper.toOpposition
import com.ark.stabot.infrastructure.persistent.mapper.OppositionMapper.toOppositionEntity
import com.ark.stabot.model.Trademark
import com.ark.stabot.utils.toOppList
import com.ark.stabot.utils.toOppString

object TrademarkMapper {

    fun TrademarkEntity.toTrademark(): Trademark {
        return Trademark(
            applicationNumber = this.applicationNumber,
            status = this.status,
            tmClass = this.tmClass,
            dateOfApplication = this.dateOfApplication,
            appropriateOffice = this.appropriateOffice,
            state = this.state,
            country = this.country,
            filingMode = this.filingMode,
            tmAppliedFor = this.tmAppliedFor,
            tmCategory = this.tmCategory,
            tmType = this.tmType,
            userDetails = this.userDetails,
            certDetail = this.certDetail,
            validUpTo = this.validUpTo,
            proprietorName = this.proprietorName,
            proprietorAddress = this.proprietorAddress,
            emailId = this.emailId,
            agentName = this.agentName,
            agentAddress = this.agentAddress,
            publicationDetails = this.publicationDetails,
            serviceDetails = this.serviceDetails,
            oppositions = this.oppositions.map { it.toOpposition() }.toMutableList(),
            oppositionsAlt = this.oppositionsAlt?.toOppList()
        )
    }

    fun Trademark.toTrademarkEntity(): TrademarkEntity {
        return TrademarkEntity(
            applicationNumber = this.applicationNumber,
            status = this.status,
            tmClass = this.tmClass,
            dateOfApplication = this.dateOfApplication,
            appropriateOffice = this.appropriateOffice,
            state = this.state,
            country = this.country,
            filingMode = this.filingMode,
            tmAppliedFor = this.tmAppliedFor,
            tmCategory = this.tmCategory,
            tmType = this.tmType,
            userDetails = this.userDetails,
            certDetail = this.certDetail,
            validUpTo = this.validUpTo,
            proprietorName = this.proprietorName,
            proprietorAddress = this.proprietorAddress,
            emailId = this.emailId,
            agentName = this.agentName,
            agentAddress = this.agentAddress,
            publicationDetails = this.publicationDetails,
            serviceDetails = this.serviceDetails,
            oppositions = this.oppositions.map { it.toOppositionEntity() }.toMutableList(),
            oppositionsAlt = this.oppositionsAlt?.toOppString()
        )
    }
}