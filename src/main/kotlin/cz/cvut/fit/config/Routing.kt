package cz.cvut.fit.config

import cz.cvut.fit.routes.authRouting
import cz.cvut.fit.routes.linkRouting
import cz.cvut.fit.routes.redirectRouting
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        route("/api/v1") {
            authRouting()
            authenticate {
                linkRouting()
            }
        }
        redirectRouting()
    }
}
