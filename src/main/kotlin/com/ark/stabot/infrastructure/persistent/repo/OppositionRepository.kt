package com.ark.stabot.infrastructure.persistent.repo

import com.ark.stabot.infrastructure.persistent.entity.OppositionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OppositionRepository: JpaRepository<OppositionEntity, Long>