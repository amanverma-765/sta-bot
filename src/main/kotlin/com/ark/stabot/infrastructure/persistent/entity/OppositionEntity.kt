package com.ark.stabot.infrastructure.persistent.entity

import jakarta.persistence.*

@Entity
@Table(name = "oppositions")
data class OppositionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "opposition_number", nullable = false, columnDefinition = "TEXT")
    val oppositionNumber: String,

    @Column(name = "opposition_date", columnDefinition = "TEXT")
    val oppositionDate: String?,

    @Column(name = "opponent_name", columnDefinition = "TEXT")
    val opponentName: String?,

    @Column(name = "opponent_code", columnDefinition = "TEXT")
    val opponentCode: String?,

    @Column(name = "opponent_addr", columnDefinition = "TEXT")
    val opponentAddr: String?,

    @Column(name = "agent_name", columnDefinition = "TEXT")
    val agentName: String?,

    @Column(name = "agent_addr", columnDefinition = "TEXT")
    val agentAddr: String?,

    @Column(name = "status", columnDefinition = "TEXT")
    val status: String?,

    @Column(name = "decision", columnDefinition = "TEXT")
    val decision: String?,

    @Column(name = "trademark_ref", columnDefinition = "TEXT")
    val trademarkRef: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trademark_id")
    var trademark: TrademarkEntity? = null
)