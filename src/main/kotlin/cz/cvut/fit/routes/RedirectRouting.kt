package cz.cvut.fit.routes

import cz.cvut.fit.service.ILinkService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.redirectRouting() {
    route("/r") {
        get("{code}") {
            val code = call.parameters["code"] ?: return@get call.respondText(
                "Page not found.",
                status = HttpStatusCode.NotFound
            )
            val linkService by closestDI().instance<ILinkService>()
            val link = linkService.findByCode(code) ?: return@get call.respondText(
                "Page not found",
                status = HttpStatusCode.NotFound
            )
            linkService.updateLinkClicks(link)
            call.respondRedirect(link.dest, true)
        }
    }
}