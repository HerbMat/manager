package com.home.transaction.manager.domain.transaction.service

import com.home.transaction.manager.domain.transaction.port.TransactionRepositoryPort
import com.home.transaction.manager.domain.transaction.model.TransactionHistory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TransactionService(val transactionRepositoryPort: TransactionRepositoryPort) {
    fun saveTransactionHistory(transactionHistory: TransactionHistory): Mono<TransactionHistory> {
        return transactionRepositoryPort.saveTransactionHistory(transactionHistory)
    }

    fun getAllTransactionHistories(page: Pageable): Flux<TransactionHistory> {
        return transactionRepositoryPort.getTransactionHistories(page)
    }
}