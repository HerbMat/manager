package com.home.transaction.manager.security.jwt

import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.util.Assert

class ClaimValidator: OAuth2TokenValidator<Jwt> {
    private val error = OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST)

    override fun validate(token: Jwt): OAuth2TokenValidatorResult {
        Assert.notNull(token, "token cannot be null")
        val claimValue: String = token.getClaim("username")
        return if (claimValue == "admin") {
            OAuth2TokenValidatorResult.success()
        } else {
            OAuth2TokenValidatorResult.failure(error)
        }
    }

}