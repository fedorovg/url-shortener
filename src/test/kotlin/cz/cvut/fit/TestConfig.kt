package cz.cvut.fit

import com.auth0.jwt.algorithms.Algorithm
import cz.cvut.fit.repository.*
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
import io.kotest.core.config.AbstractProjectConfig
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton


object ProjectConfig : AbstractProjectConfig() {
    private const val issuer = "issuer"
    private const val secret = "secret"
    private const val expiration = 36000000
    private const val realm = "realm"

    var di: DI = DI {
        bind<IUserRepository> { singleton { ArrayUserRepository() } }
        bind<ILinkRepository> { singleton { ArrayLinkRepository() } }
        bind<IPasswordService> { singleton { PasswordService() } }
        bind<IUserService> { singleton { UserService(instance(), instance()) } }
        bind<ILinkService> { singleton { LinkService("http://localhost:8080", instance()) } }
        bind<ICodeService> { singleton { CodeService(instance()) }}
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
    override fun beforeAll() {
        println("[BEFORE PROJECT] beforeAll")
    }

    override fun afterAll() {
        println("[AFTER PROJECT] afterAll")
    }
}