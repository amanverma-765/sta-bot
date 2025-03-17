package com.ark.stabot.infrastructure.persistent.mapper

import com.ark.stabot.infrastructure.persistent.entity.TrademarkEntity
import com.ark.stabot.model.Trademark

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
            serviceDetails = this.serviceDetails
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
            serviceDetails = this.serviceDetails
        )
    }
}