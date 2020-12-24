package com.home.transaction.manager.security.jwt

import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import spock.lang.Specification
import spock.lang.Subject

import java.time.Instant

class ClaimValidatorSpec extends Specification {

    @Subject
    def claimValidator = new ClaimValidator()

    def "It validates successfully"() {
        given:
            def token = mockToken(Map.of("username", "admin"))
        when:
            def result = claimValidator.validate(token)
        then:
            result == OAuth2TokenValidatorResult.success()
    }

    def "It fails because of bad user"() {
        given:
            def token = mockToken(Map.of("username", "badUser"))
        when:
            def result = claimValidator.validate(token)
        then:
            result.getErrors().size() == 1
            result.getErrors().iterator().next().getErrorCode() == OAuth2ErrorCodes.INVALID_REQUEST
    }

    def "It fails because of missing username claim"() {
        given:
            def token = mockToken(Map.of("badClaim", "badClaimValue"))
        when:
            claimValidator.validate(token)
        then:
            def ex = thrown(IllegalStateException)
            ex.message == "token.getClaim(\"username\") must not be null"
    }

    def mockToken(def claims) {
        return new Jwt(
                "abc",
                Instant.now(),
                Instant.now(),
                Map.of("alg", "HS256"),
                claims
        )
    }
}
