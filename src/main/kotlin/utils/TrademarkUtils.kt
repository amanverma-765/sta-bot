package com.ark.utils

import com.ark.model.Trademark

fun createEmptyTrademark(applicationId: String): Trademark {
    return Trademark(
        applicationNumber = applicationId,
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
        publicationDetails = "NA"
    )
}