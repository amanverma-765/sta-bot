package com.ark.model

import kotlinx.serialization.Serializable


@Serializable
data class Trademark(
    val status: String? = null,
    val applicationNumber: String,
    val tmClass: String,
    val dateOfApplication: String? = null,
    val appropriateOffice: String? = null,
    val state: String? = null,
    val country: String? = null,
    val filingMode: String? = null,
    val tmAppliedFor: String,
    val tmCategory: String? = null,
    val tmType: String? = null,
    val userDetails: String? = null,
    val certDetail: String? = null,
    val validUpTo: String? = null,
    val proprietorName: String? = null,
    val proprietorAddress: String? = null,
    val emailId: String? = null,
    val agentName: String? = null,
    val agentAddress: String? = null,
    val publicationDetails: String? = null
)