package com.home.transaction.manager.rest.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.home.transaction.manager.DataLoader
import com.home.transaction.manager.config.IntegrationTest
import com.home.transaction.manager.domain.transaction.model.TransactionHistory
import com.home.transaction.manager.mongo.repository.TransactionHistoryRepository
import com.home.transaction.manager.rest.dto.TransactionRequest
import com.home.transaction.manager.security.jwt.ClaimValidator
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import spock.lang.Specification

@IntegrationTest
class TransactionHistoryControllerSpec extends Specification {
    private final static String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNTk0MjI2NjE4LCJleHAiOjE1OTQyMjg0MTh9.TunTQRWmXkLCfOx20UdnYjlSy4_OdhSVamJyAbiEkYA"

    @SpringBean
    private OAuth2TokenValidator<Jwt> oAuth2TokenValidator = new DelegatingOAuth2TokenValidator<Jwt>(new ClaimValidator())

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    private WebTestClient webTestClient

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository

    @Autowired
    MongoProperties mongoProperties;

    @Autowired
    DataLoader dataLoader;

    def cleanup() {
        transactionHistoryRepository.deleteAll().block()
    }

    def "It stores transaction successfully"() {
        given:
            def transactionHistoryRequest = new TransactionRequest("Natural Gas", "It rose as expected", true)
        when:
            def response = webTestClient
                    .post()
                    .uri("/history")
                    .headers({ headers -> headers.setBearerAuth(TOKEN) })
                    .body(BodyInserters.fromValue(transactionHistoryRequest))
                    .headers({ headers -> headers.setBearerAuth(TOKEN) })
                    .exchange()
                    .returnResult(String.class)
        then:
            response.status == HttpStatus.CREATED
            def createdTransactionHistory = transactionHistoryRepository.findById(getIdFromPath(response.responseHeaders.get(HttpHeaders.LOCATION).get(0))).block()
            createdTransactionHistory.title == transactionHistoryRequest.title
            createdTransactionHistory.content == transactionHistoryRequest.content
            createdTransactionHistory.goodTransaction == transactionHistoryRequest.goodChoice
    }

    def "it should lists transactions"() {
        given:
            dataLoader.load("transaction-history-data.json", "transactionHistoryEntity")
        when:
            def response = webTestClient
                    .get()
                    .uri("/history")
                    .headers({ headers -> headers.setBearerAuth(TOKEN) })
                    .exchange()
                    .returnResult(TransactionHistory)
        then:
            response.status == HttpStatus.OK
        and:
            verifyAll(response.responseBody.collectList().block()) {
                it.size() == 1
                it.get(0).title == "Natural Gas"
                it.get(0).content == "It rose as expected"
                it.get(0).isGoodTransaction == true
        }
    }

    private def getIdFromPath(String path) {
        return path.split("/").last()
    }
}
