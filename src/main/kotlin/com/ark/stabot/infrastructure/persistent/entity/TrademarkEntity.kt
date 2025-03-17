package com.ark.stabot.infrastructure.persistent.entity

import jakarta.persistence.*

@Table(name = "universal_trademarks")
data class TrademarkEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tm_id")
    val id: Long? = null,

    @Column(name = "application_number", nullable = false, unique = true)
    val applicationNumber: String,

    @Column(name = "status")
    val status: String?,

    @Column(name = "tm_class", nullable = false)
    val tmClass: String,

    @Column(name = "date_of_application")
    val dateOfApplication: String? = null,

    @Column(name = "appropriate_office")
    val appropriateOffice: String? = null,

    @Column(name = "state")
    val state: String? = null,

    @Column(name = "country")
    val country: String? = null,

    @Column(name = "filing_mode")
    val filingMode: String? = null,

    @Column(name = "tm_applied_for", nullable = false)
    val tmAppliedFor: String,

    @Column(name = "tm_category")
    val tmCategory: String? = null,

    @Column(name = "tm_type")
    val tmType: String? = null,

    @Column(name = "user_details")
    val userDetails: String? = null,

    @Column(name = "cert_detail")
    val certDetail: String? = null,

    @Column(name = "valid_up_to")
    val validUpTo: String? = null,

    @Column(name = "proprietor_name")
    val proprietorName: String? = null,

    @Column(name = "proprietor_address")
    val proprietorAddress: String? = null,

    @Column(name = "email_id")
    val emailId: String? = null,

    @Column(name = "agent_name")
    val agentName: String? = null,

    @Column(name = "agent_address")
    val agentAddress: String? = null,

    @Column(name = "publication_details")
    val publicationDetails: String? = null,

    @Column(name = "service_details", columnDefinition = "TEXT")
    val serviceDetails: String? = null


)
