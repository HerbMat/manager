package com.home.transaction.manager.sso.facade

import com.home.transaction.manager.config.properties.SsoProperties
import com.home.transaction.manager.sso.dto.SsoConfResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class SsoFacade(private val webClient: WebClient, private val ssoProperties: SsoProperties) {
    fun getSecret(): Mono<String> {
        return webClient.get()
                .uri(ssoProperties.uri)
                .exchange()
                .flatMap {
                    clientResponse -> clientResponse.toEntity(SsoConfResponse::class.java).map {
                        responseEntity -> responseEntity.body?.secret
                    }
                }
    }
}