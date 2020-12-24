package com.home.transaction.manager.memory.repository

import com.home.transaction.manager.sso.facade.SsoFacade
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

class SsoRepository(private val ssoFacade: SsoFacade) {
    lateinit var secret: String
        private set

    init {
        reloadSecret()
    }

    fun reloadSecret() {
        ssoFacade.getSecret()
                .doOnSuccess{ secret = it }
                .subscribe()
    }
}