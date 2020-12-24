package com.home.transaction.manager.rest.extensions

import com.home.transaction.manager.domain.transaction.model.TransactionHistory
import com.home.transaction.manager.rest.dto.TransactionRequest

fun TransactionRequest.toTransactionHistory() = TransactionHistory(
        title = title,
        content = content,
        isGoodTransaction =  isGoodChoice,
        id = null
)













