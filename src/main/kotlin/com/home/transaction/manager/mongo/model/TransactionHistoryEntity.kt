package com.home.transaction.manager.mongo.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class TransactionHistoryEntity(
        @Id
        val id: String?,
        val content: String,
        val title: String,
        val isGoodTransaction: Boolean)