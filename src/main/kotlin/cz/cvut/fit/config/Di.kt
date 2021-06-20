package cz.cvut.fit.config

import com.auth0.jwt.algorithms.Algorithm
import cz.cvut.fit.repository.ILinkRepository
import cz.cvut.fit.repository.IUserRepository
import cz.cvut.fit.repository.LinkRepository
import cz.cvut.fit.repository.UserRepository
import cz.cvut.fit.service.CodeService
import cz.cvut.fit.service.ICodeService
import cz.cvut.fit.service.IJwtProvider
import cz.cvut.fit.service.JwtProvider
import cz.cvut.fit.service.ILinkService
import cz.cvut.fit.service.LinkService
import cz.cvut.fit.service.PasswordService
import cz.cvut.fit.service.IPasswordService
import cz.cvut.fit.service.IUserService
import cz.cvut.fit.service.UserService
import io.ktor.application.*
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.ktor.di
import org.kodein.di.singleton

fun Application.configureDi() {
    val issuer = environment.config.property("jwt.domain").getString()
    val secret = environment.config.property("jwt.secret").getString()
    val expiration = environment.config.property("jwt.expiration").getString().toInt()
    val realm = environment.config.property("jwt.realm").getString()
    val baseUrl = environment.config.property("app.baseUrl").getString()
    di {
        bind<IUserRepository> { singleton { UserRepository() } }
        bind<ILinkRepository> { singleton { LinkRepository() } }
        bind<IPasswordService> { singleton { PasswordService() } }
        bind<IUserService> { singleton { UserService(instance(), instance()) } }
        bind<ILinkService> { singleton { LinkService(baseUrl, instance()) }}
        bind<ICodeService> { singleton { CodeService(instance()) } }
        bind<IJwtProvider> {
            singleton {
                JwtProvider(
                    Algorithm.HMAC256(secret),
                    expiration,
                    issuer,
                    realm,
                    instance()
                )
            }
        }
    }
}

fun Application.testDi() {
    val issuer = "issuer"
    val secret = "secret"
    val expiration = 360000
    val realm = "realm"
    val baseUrl = "http://localhost:8080"


    di {
        bind<IUserRepository> { singleton { UserRepository() } }
        bind<ILinkRepository> { singleton { LinkRepository() } }
        bind<IPasswordService> { singleton { PasswordService() } }
        bind<IUserService> { singleton { UserService(instance(), instance()) } }
        bind<ILinkService> { singleton { LinkService(baseUrl, instance()) }}
        bind<ICodeService> { singleton { CodeService(instance()) } }
        bind<IJwtProvider> {
            singleton {
                JwtProvider(
                    Algorithm.HMAC256(secret),
                    expiration,
                    issuer,
                    realm,
                    instance()
                )
            }
        }
    }
}