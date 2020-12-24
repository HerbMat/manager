package com.home.transaction.manager.memory.config

import com.home.transaction.manager.memory.repository.SsoRepository
import com.home.transaction.manager.sso.facade.SsoFacade
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class SsoConfig(private val ssoFacade: SsoFacade) {

    @Bean
    fun ssoRepository(ssoFacade: SsoFacade): SsoRepository {
        return SsoRepository(ssoFacade)
    }
}