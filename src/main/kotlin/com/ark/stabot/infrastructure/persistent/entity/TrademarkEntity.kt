package com.ark.stabot.infrastructure.persistent.entity

import jakarta.persistence.*

@Entity
@Table(name = "universal_trademarks")
data class TrademarkEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "application_number", nullable = false, unique = true, columnDefinition = "TEXT")
    val applicationNumber: String,

    @Column(name = "status", nullable = false, columnDefinition = "TEXT")
    val status: String,

    @Column(name = "tm_class", nullable = false, columnDefinition = "TEXT")
    val tmClass: String,

    @Column(name = "date_of_application", columnDefinition = "TEXT")
    val dateOfApplication: String? = null,

    @Column(name = "appropriate_office", columnDefinition = "TEXT")
    val appropriateOffice: String? = null,

    @Column(name = "state", columnDefinition = "TEXT")
    val state: String? = null,

    @Column(name = "country", columnDefinition = "TEXT")
    val country: String? = null,

    @Column(name = "filing_mode", columnDefinition = "TEXT")
    val filingMode: String? = null,

    @Column(name = "tm_applied_for", nullable = false, columnDefinition = "TEXT")
    val tmAppliedFor: String,

    @Column(name = "tm_category", columnDefinition = "TEXT")
    val tmCategory: String? = null,

    @Column(name = "tm_type", columnDefinition = "TEXT")
    val tmType: String? = null,

    @Column(name = "user_details", columnDefinition = "TEXT")
    val userDetails: String? = null,

    @Column(name = "cert_detail", columnDefinition = "TEXT")
    val certDetail: String? = null,

    @Column(name = "valid_up_to", columnDefinition = "TEXT")
    val validUpTo: String? = null,

    @Column(name = "proprietor_name", columnDefinition = "TEXT")
    val proprietorName: String? = null,

    @Column(name = "proprietor_address", columnDefinition = "TEXT")
    val proprietorAddress: String? = null,

    @Column(name = "email_id", columnDefinition = "TEXT")
    val emailId: String? = null,

    @Column(name = "agent_name", columnDefinition = "TEXT")
    val agentName: String? = null,

    @Column(name = "agent_address", columnDefinition = "TEXT")
    val agentAddress: String? = null,

    @Column(name = "publication_details", columnDefinition = "TEXT")
    val publicationDetails: String? = null,

    @Column(name = "service_details", columnDefinition = "TEXT")
    val serviceDetails: String? = null,

    @OneToMany(mappedBy = "trademark", cascade = [CascadeType.ALL], orphanRemoval = true)
    val oppositions: MutableList<OppositionEntity> = mutableListOf(),

    @Column(name = "oppositions_alt", columnDefinition = "TEXT")
    val oppositionsAlt: String? = null
)