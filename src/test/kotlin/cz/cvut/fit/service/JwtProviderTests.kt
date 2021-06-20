package cz.cvut.fit.service

import cz.cvut.fit.ProjectConfig
import cz.cvut.fit.model.UserDto
import cz.cvut.fit.service.IJwtProvider
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.auth.jwt.*
import org.kodein.di.instance

class JwtProviderTests : StringSpec() {
    val jwtProvider by ProjectConfig.di.instance<IJwtProvider>()
    val userService by ProjectConfig.di.instance<IUserService>()
    init {
        "Provider should work with existing user" {
            val user = UserDto(1, "log1", "passwd")
            userService.registerUser(user.login, user.password) // User is registered in this test
            val verifier = jwtProvider.buildVerifier()
            val validator = jwtProvider.buildValidator()
            val token = jwtProvider.issueToken(user)
            val decoded = verifier.verify(token)
            val res = validator(JWTCredential(decoded))
            res?.payload?.claims?.get("LOGIN_CLAIM")?.asString() shouldBe "log1"
        }
        "Provider should fail with nonexistent user" {
            // Setup
            val user = UserDto(1, "does not exist", "passwd")
            val verifier = jwtProvider.buildVerifier()
            val validator = jwtProvider.buildValidator()
            val token = jwtProvider.issueToken(user)
            val decoded = verifier.verify(token)
            val res = validator(JWTCredential(decoded))
            res?.payload?.claims?.get("LOGIN_CLAIM")?.asString() shouldBe null
        }
    }
}