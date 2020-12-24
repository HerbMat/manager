package com.home.transaction.manager.config.listeners

import com.home.transaction.manager.memory.repository.SsoRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.event.ApplicationStartingEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class SsoInitListener(private val ssoRepository: SsoRepository): ApplicationListener<ApplicationStartingEvent> {
    override fun onApplicationEvent(event: ApplicationStartingEvent) {
        ssoRepository.reloadSecret()
    }
}