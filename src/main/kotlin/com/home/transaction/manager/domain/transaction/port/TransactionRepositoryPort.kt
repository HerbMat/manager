package com.home.transaction.manager.domain.transaction.port

import com.home.transaction.manager.domain.transaction.model.TransactionHistory
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TransactionRepositoryPort {
    fun saveTransactionHistory(transactionHistory: TransactionHistory): Mono<TransactionHistory>
    fun getTransactionHistories(page: Pageable): Flux<TransactionHistory>
}