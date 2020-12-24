package com.home.transaction.manager.config


import com.home.transaction.manager.config.properties.SsoProperties
import com.home.transaction.manager.memory.repository.SsoRepository
import com.home.transaction.manager.security.jwt.ClaimValidator
import com.home.transaction.manager.security.jwt.LocalJWTDecoder
import com.home.transaction.manager.sso.facade.SsoFacade
import com.nimbusds.jose.crypto.MACVerifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtTimestampValidator
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.client.WebClient


@Configuration
@EnableWebFlux
@EnableConfigurationProperties(SsoProperties::class)
class WebConfig : WebFluxConfigurer {

    @Value("\${app.jwt.secret}")
    private lateinit var secret: String

    @Bean
    fun oAuth2TokenValidator(): OAuth2TokenValidator<Jwt> {
        return DelegatingOAuth2TokenValidator<Jwt>(JwtTimestampValidator(), ClaimValidator())
    }

    @Bean
    fun jwtDecoder(oAuth2TokenValidator: OAuth2TokenValidator<Jwt>, ssoRepository: SsoRepository): ReactiveJwtDecoder {
        return LocalJWTDecoder(
                MappedJwtClaimSetConverter.withDefaults(emptyMap()),
                oAuth2TokenValidator,
                MACVerifier(ssoRepository.secret)
        )
    }

    @Bean
    fun webClient(): WebClient {
        return WebClient.create()
    }


    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
                .allowedMethods("*")
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/")
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
    }
}