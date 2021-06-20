package cz.cvut.fit

import cz.cvut.fit.config.*
import io.ktor.application.*
import io.ktor.server.netty.*


fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module(testing: Boolean = false) {
    if (!testing) {
        configureDi()
    } else {
        testDi()
    }
    configureDatabase()
    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
