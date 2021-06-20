package cz.cvut.fit.routes

import cz.cvut.fit.service.ICodeService
import cz.cvut.fit.service.ILinkService
import cz.cvut.fit.service.IUserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

private data class From(val from: String)

fun Route.linkRouting() {
    route("/link") {
            post {
                val linkService by closestDI().instance<ILinkService>()
                val codeService by closestDI().instance<ICodeService>()
                val userService by closestDI().instance<IUserService>()

                val jwtPrincipal = call.authentication.principal<JWTPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val (from) = kotlin.runCatching {
                    call.receive<From>()
                }.getOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)

                val userLogin = jwtPrincipal?.payload?.claims?.get("LOGIN_CLAIM")?.asString()
                    ?: return@post call.respond(HttpStatusCode.InternalServerError)

                val user = userService.findByLogin(userLogin)
                    ?: return@post call.respond(HttpStatusCode.InternalServerError)

                val code = codeService.generate(from, user.id)

                val l = linkService.findByCode(code)
                if (l != null) {
                    return@post call.respond(l)
                }

                val link = linkService.simpleAddLink(user.id, from, code)
                    ?: return@post call.respond(HttpStatusCode.InternalServerError)

                call.respond(HttpStatusCode.Created, link)
            }

        get {
            val linkService by closestDI().instance<ILinkService>()
            val userService by closestDI().instance<IUserService>()

            val jwtPrincipal = call.authentication.principal<JWTPrincipal>()

            val userLogin = jwtPrincipal?.payload?.claims?.get("LOGIN_CLAIM")?.asString()
                ?: return@get call.respond(HttpStatusCode.InternalServerError)

            val user = userService.findByLogin(userLogin)
                ?: return@get call.respond(HttpStatusCode.InternalServerError)

            val links = linkService.findAllByUser(user.id)
            call.respond(links)
        }
        get("{id}") {
            val linkService by closestDI().instance<ILinkService>()
            val linkId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText(
                "No link id.",
                status = HttpStatusCode.BadRequest
            )
            val link = linkService.findById(linkId) ?: return@get call.respondText(
                "Not found.",
                status = HttpStatusCode.NotFound
            )
            call.respond(link)
        }

    }
}