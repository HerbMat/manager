package com.home.transaction.manager.mongo.repository

import com.home.transaction.manager.mongo.model.TransactionHistoryEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TransactionHistoryRepository: ReactiveMongoRepository<TransactionHistoryEntity, String> {
    fun findAllByIdNotNull(page: Pageable): Flux<TransactionHistoryEntity>
}