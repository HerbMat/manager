package com.home.transaction.manager.security.jwt

import com.nimbusds.jose.JWSVerifier
import org.springframework.core.convert.converter.Converter
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jwt.JwtValidationException
import spock.lang.Specification
import spock.lang.Subject

class LocalJWTDecoderSpec extends Specification {
    private final static String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNTk0MjI2NjE4LCJleHAiOjE1OTQyMjg0MTh9.TunTQRWmXkLCfOx20UdnYjlSy4_OdhSVamJyAbiEkYA"

    private Converter<Map<String, Object>, Map<String, Object>> claimSetConverter
    private OAuth2TokenValidator<Jwt> jwtValidator
    private JWSVerifier jwsVerifier

    @Subject
    private LocalJWTDecoder localJWTDecoder

    def setup() {
        claimSetConverter = Mock(Converter)
        jwtValidator = Mock(OAuth2TokenValidator)
        jwsVerifier = Mock(JWSVerifier)
        localJWTDecoder = new LocalJWTDecoder(claimSetConverter, jwtValidator, jwsVerifier)
    }

    def "It should decode successfully"() {
        when:
            def result = localJWTDecoder.decode(TOKEN).block()
        then:
            result != null
            result.headers.get("typ") == "JWT"
            result.headers.get("alg") == "HS256"
            result.claims.get("username") == "admin"
            result.getTokenValue() == TOKEN
        and:
            1 * jwtValidator.validate(_) >> OAuth2TokenValidatorResult.success()
            1 * claimSetConverter.convert(_) >> Map.of("username", "admin")
            1 * jwsVerifier.verify(_, _ , _) >> true
    }

    def "It should fail validation"() {
        when:
            localJWTDecoder.decode(TOKEN).block()
        then:
            def ex = thrown(JwtValidationException)
            ex.errors.size() == 1
            ex.errors[0].errorCode == OAuth2ErrorCodes.INVALID_REQUEST
        and:
            1 * jwtValidator.validate(_) >> OAuth2TokenValidatorResult.failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST))
            1 * claimSetConverter.convert(_) >> Map.of("username", "admin")
            1 * jwsVerifier.verify(_, _ , _) >> true
    }

    def "It should fail signature validation"() {
        when:
            localJWTDecoder.decode(TOKEN).block()
        then:
            def ex = thrown(JwtException)
            ex.message == "Bad signature"
        and:
            0 * jwtValidator.validate(_)
            0 * claimSetConverter.convert(_)
            1 * jwsVerifier.verify(_, _ , _) >> false
    }

    def "It should fail claim conversion"() {
        when:
            localJWTDecoder.decode(TOKEN).block()
        then:
            def ex = thrown(BadJwtException)
            ex.message == "An error occurred while attempting to decode the Jwt: exception"
        and:
            0 * jwtValidator.validate(_)
            1 * claimSetConverter.convert(_) >> {
                throw new IllegalArgumentException("exception")
            }
            1 * jwsVerifier.verify(_, _ , _) >> true
    }

    def "It should fail parsing jwt"() {
        when:
            localJWTDecoder.decode("bad token").block()
        then:
            def ex = thrown(Exception)
            ex.message == "java.text.ParseException: Invalid JWT serialization: Missing dot delimiter(s)"
        and:
            0 * jwtValidator.validate(_)
            0 * claimSetConverter.convert(_)
            0 * jwsVerifier.verify(_, _ , _)
    }

}
