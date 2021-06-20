package cz.cvut.fit.model

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


data class UserDto(val id: Int,
                   val login: String,
                   val password: String)


object Users : IntIdTable() {
    val login = varchar("login", 30).uniqueIndex()
    val password = varchar("password", 60)
}

class User(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, User>(Users)
    var login by Users.login
    var password by Users.password
    val links by Link referrersOn Links.user

    fun toDto() = UserDto(id.value, login, password)
}

