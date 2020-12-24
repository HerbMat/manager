package com.home.transaction.manager.security.jwt

import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT
import org.springframework.core.convert.converter.Converter
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.*
import reactor.core.publisher.Mono


class LocalJWTDecoder(
        private val claimSetConverter: Converter<Map<String, Any>, Map<String, Any>>,
        private val jwtValidator: OAuth2TokenValidator<Jwt>,
        private val jwsVerifier: JWSVerifier
) : ReactiveJwtDecoder {
    override fun decode(token: String?): Mono<Jwt> {
        return try {
            Mono.just(token!!)
                    .map { JWTParser.parse(token) }
                    .map { validateSignature(it as SignedJWT) }
                    .map { createJwt(it) }
                    .map { validateJwt(it) }
        } catch (ex: JwtException) {
            throw ex
        } catch (ex: RuntimeException) {
            throw JwtException("An error occurred while attempting to decode the Jwt: " + ex.message, ex)
        }
    }

    private fun createJwt(parsedJwt: JWT): Jwt {
        return try {
            val jwtBuilder = Jwt.withTokenValue(parsedJwt.parsedString)
            parsedJwt.header.toJSONObject().forEach { key, value -> jwtBuilder.header(key, value) }
            claimSetConverter.convert(parsedJwt.jwtClaimsSet.claims)?.forEach { (key, value) -> jwtBuilder.claim(key, value) }
            jwtBuilder.build()
        } catch (ex: Exception) {
            throw BadJwtException("An error occurred while attempting to decode the Jwt: " + ex.message, ex)
        }
    }

    private fun validateJwt(jwt: Jwt): Jwt {
        val result: OAuth2TokenValidatorResult = this.jwtValidator.validate(jwt)
        if (result.hasErrors()) {
            val message = result.errors.iterator().next().description
            throw JwtValidationException(message, result.errors)
        }
        return jwt
    }

    private fun validateSignature(jwt: SignedJWT): JWT {
        if (!jwsVerifier.verify(jwt.header, jwt.signingInput, jwt.signature)) {
            throw JwtException("Bad signature")
        }
        return jwt
    }

}