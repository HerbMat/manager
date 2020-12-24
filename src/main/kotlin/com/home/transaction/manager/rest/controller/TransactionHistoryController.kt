package com.home.transaction.manager.rest.controller;

import com.home.transaction.manager.domain.transaction.model.TransactionHistory
import com.home.transaction.manager.domain.transaction.port.TransactionRepositoryPort
import com.home.transaction.manager.domain.transaction.service.TransactionService
import com.home.transaction.manager.rest.dto.TransactionRequest
import com.home.transaction.manager.rest.extensions.toTransactionHistory
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@RestController
@RequestMapping("/history")
class TransactionHistoryController(private val transactionService: TransactionService) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun storeTransaction(@RequestBody transactionRequest: TransactionRequest, httpRequest: ServerHttpRequest): Mono<ResponseEntity<Nothing>> {
        return transactionService.saveTransactionHistory(transactionRequest.toTransactionHistory())
                .map { transactionHistory -> ResponseEntity.created(
                        UriComponentsBuilder.fromHttpRequest(httpRequest)
                            .path("/${transactionHistory.id}")
                            .build().toUri())
                        .body(null)
                }
    }

    @GetMapping(produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    fun getTransactions(
            @RequestParam(name = "page", defaultValue = "0") page: Int,
            @RequestParam(name = "size", defaultValue = "10") size: Int
    ): Flux<TransactionHistory> {
        return transactionService.getAllTransactionHistories(PageRequest.of(page, size))
    }
}