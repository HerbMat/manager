package com.home.transaction.manager.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "app.sso")
@Validated
data class SsoProperties(
        var uri: String = ""
)