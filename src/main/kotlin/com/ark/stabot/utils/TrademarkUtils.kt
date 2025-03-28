package com.ark.stabot.utils

import com.ark.stabot.model.Trademark

fun createEmptyTrademark(applicationId: String, parsingError: Boolean = false): Trademark {
    val defaultValue = if (parsingError) "parsingErr" else "NA"

    return Trademark(
        applicationNumber = applicationId,
        status = defaultValue,
        tmClass = defaultValue,
        dateOfApplication = defaultValue,
        appropriateOffice = defaultValue,
        state = defaultValue,
        country = defaultValue,
        filingMode = defaultValue,
        tmAppliedFor = defaultValue,
        tmCategory = defaultValue,
        tmType = defaultValue,
        userDetails = defaultValue,
        certDetail = defaultValue,
        validUpTo = defaultValue,
        proprietorName = defaultValue,
        proprietorAddress = defaultValue,
        emailId = defaultValue,
        agentName = defaultValue,
        agentAddress = defaultValue,
        publicationDetails = defaultValue,
        oppositions = mutableListOf()
    )
}