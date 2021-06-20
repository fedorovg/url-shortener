package cz.cvut.fit.config

import io.ktor.http.*
import io.ktor.application.*
import io.ktor.features.*

fun Application.configureHTTP() {
    install(CORS) {
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Delete)
        anyHost()
    }
}
