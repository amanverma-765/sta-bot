package com.ark.stabot.utils

import com.ark.stabot.model.Trademark

fun createEmptyTrademark(applicationId: String, parsingError: Boolean = false): Trademark {
    return Trademark(
        applicationNumber = if (parsingError) "ParsingErr" else applicationId,
        status = "NA",
        tmClass = "NA",
        dateOfApplication = "NA",
        appropriateOffice = "NA",
        state = "NA",
        country = "NA",
        filingMode = "NA",
        tmAppliedFor = "NA",
        tmCategory = "NA",
        tmType = "NA",
        userDetails = "NA",
        certDetail = "NA",
        validUpTo = "NA",
        proprietorName = "NA",
        proprietorAddress = "NA",
        emailId = "NA",
        agentName = "NA",
        agentAddress = "NA",
        publicationDetails = "NA",
        oppositions = mutableListOf()
    )
}