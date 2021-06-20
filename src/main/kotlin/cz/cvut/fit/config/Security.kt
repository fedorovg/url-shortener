package cz.cvut.fit.config

import io.ktor.auth.*
import io.ktor.auth.jwt.*
import cz.cvut.fit.service.IJwtProvider
import io.ktor.application.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI


fun Application.configureSecurity() {
    val jwtProvider by closestDI().instance<IJwtProvider>()
    val validator = jwtProvider.buildValidator()
    authentication {
        jwt {
            realm = jwtProvider.realm
            verifier( jwtProvider.buildVerifier() )
            validate { validator(it) }
            }
    }
}
