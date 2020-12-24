package com.home.transaction.manager.mongo.extensions

import com.home.transaction.manager.domain.transaction.model.TransactionHistory
import com.home.transaction.manager.mongo.model.TransactionHistoryEntity

fun TransactionHistory.toTransactionHistoryEntity() = TransactionHistoryEntity(
        title = title,
        content = content,
        isGoodTransaction =  isGoodTransaction,
        id = id
)

fun TransactionHistoryEntity.toTransactionHistory() = TransactionHistory(
        title = title,
        content = content,
        isGoodTransaction = isGoodTransaction,
        id = id
)













