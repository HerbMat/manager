package com.home.transaction.manager.mongo.adapter

import com.home.transaction.manager.domain.transaction.port.TransactionRepositoryPort
import com.home.transaction.manager.domain.transaction.model.TransactionHistory
import com.home.transaction.manager.mongo.extensions.toTransactionHistory
import com.home.transaction.manager.mongo.extensions.toTransactionHistoryEntity
import com.home.transaction.manager.mongo.repository.TransactionHistoryRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class TransactionRepositoryAdapter(val transactionHistoryRepository: TransactionHistoryRepository): TransactionRepositoryPort {

    override fun saveTransactionHistory(transactionHistory: TransactionHistory): Mono<TransactionHistory> {
        return transactionHistoryRepository.save(
                transactionHistory.toTransactionHistoryEntity()
        ).map { transactionHistoryEntity -> transactionHistoryEntity.toTransactionHistory() }
    }

    override fun getTransactionHistories(page: Pageable): Flux<TransactionHistory> {
        return transactionHistoryRepository.findAllByIdNotNull(page)
                .map { transactionHistoryEntity -> transactionHistoryEntity.toTransactionHistory() }
    }
}