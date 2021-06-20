package cz.cvut.fit.routes

import cz.cvut.fit.service.IJwtProvider
import cz.cvut.fit.service.IPasswordService
import cz.cvut.fit.service.IUserService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

data class LoginPassword(val login: String, val password: String)
data class ResponseMessage(val message: String)
data class AuthMessage(val token: String, val id: Int)

fun Route.authRouting() {
    route("/register") {
        get {
            call.respondText { "register" }
        }
        post {
            val userService by closestDI().instance<IUserService>()
            val (login, password) = kotlin.runCatching {
                call.receive<LoginPassword>()
            }.getOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)


            if (login.isBlank() || password.isBlank()) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ResponseMessage("Invalid login or password.")
                )
            }
            if (userService.doesUserExist(login)) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ResponseMessage("User already exists")
                )
            }
            userService.registerUser(login, password)
            call.respond(HttpStatusCode.Created)
        }
    }
    route("/login") {
        post {
            val userService by closestDI().instance<IUserService>()
            val jwtProvider by closestDI().instance<IJwtProvider>()


            val (login, password) = kotlin.runCatching {
                call.receive<LoginPassword>()
            }.getOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)

            if (login.isBlank() || password.isBlank()) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ResponseMessage("Missing login or password.")
                )
            }
            val user = userService.findByLogin(login)
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ResponseMessage("User does not exist!")
                )
            if (!userService.compare(user.password, password)) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ResponseMessage("Wrong password.")
                )
            }
            val token = jwtProvider.issueToken(user)
            call.respond(HttpStatusCode.OK, AuthMessage(token = token, id = user.id))
        }
    }
}
