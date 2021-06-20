package cz.cvut.fit.config

import cz.cvut.fit.model.Links
import cz.cvut.fit.model.Users
import io.ktor.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

object H2Database {
    private const val connectionString = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
    private const val driver = "org.h2.Driver"

    val db by lazy { // Database connection must be created once, otherwise memory will leak.
        Database.connect(connectionString, driver)
    }
}

fun Application.configureDatabase() {
    TransactionManager.defaultDatabase = H2Database.db
    transaction {
        SchemaUtils.create(Users, Links)
    }
}