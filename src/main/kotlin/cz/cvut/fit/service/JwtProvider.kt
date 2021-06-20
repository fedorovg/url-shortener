package cz.cvut.fit.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import cz.cvut.fit.model.UserDto
import io.ktor.auth.jwt.*
import java.util.*


interface IJwtProvider {
    fun buildVerifier(): JWTVerifier
    fun issueToken(user: UserDto): String
    fun buildValidator(): (JWTCredential) -> JWTPrincipal?
    val realm: String
}

class JwtProvider(
    private val algorithm: Algorithm,
    private val expirationPeriod: Int,
    private val issuer: String,
    override val realm: String,
    private val userService: IUserService
) : IJwtProvider {

    companion object {
        private const val LOGIN_CLAIM = "LOGIN_CLAIM"
        private const val USER_ID_CLAIM = "USER_ID_CLAIM"

    }

    override fun buildVerifier(): JWTVerifier =
        JWT.require(algorithm)
            .withIssuer(issuer)
            .build()

    override fun issueToken(user: UserDto): String =
        JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim(LOGIN_CLAIM, user.login)
            .withClaim(USER_ID_CLAIM, user.id)
            .withExpiresAt(expirationDate())
            .sign(algorithm)

    override fun buildValidator(): (JWTCredential) -> JWTPrincipal? = { jwtCredential: JWTCredential ->
        jwtCredential.payload.claims[LOGIN_CLAIM]?.asString()?.let { login ->
            if (userService.doesUserExist(login)) {
                JWTPrincipal(jwtCredential.payload)
            } else {
                null
            }
        }
    }

    private fun expirationDate() = Date(System.currentTimeMillis() + expirationPeriod)
}