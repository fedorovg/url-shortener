package cz.cvut.fit.repository

import cz.cvut.fit.model.User
import cz.cvut.fit.model.UserDto
import cz.cvut.fit.model.Users
import org.jetbrains.exposed.sql.transactions.transaction

interface IUserRepository {
    fun findById(id: Int): UserDto?
    fun findByLogin(login: String): UserDto?
    fun create(userDto: UserDto): UserDto?
    fun deleteById(id: Int)
}

class UserRepository : IUserRepository {
    override fun findById(id: Int): UserDto? = transaction {
        User.findById(id)?.toDto()
    }

    override fun findByLogin(login: String): UserDto? = transaction {
        User.find { Users.login eq login }.firstOrNull()?.toDto()
    }

    override fun create(userDto: UserDto): UserDto? = kotlin.runCatching {
        val (_, login, password) = userDto
        transaction {
            User.new {
                this.login = login
                this.password = password
            }.toDto()
        }
        // Returns null, when insert fails, for example when unique constraints are violated
    }.getOrNull()

    override fun deleteById(id: Int): Unit = transaction {
        User.findById(id)?.delete()
    }
}

class ArrayUserRepository : IUserRepository {
    private val users = mutableListOf<UserDto>()

    override fun findById(id: Int): UserDto? = users.find { it.id == id }

    override fun findByLogin(login: String): UserDto? = users.find { it.login == login }

    override fun create(userDto: UserDto): UserDto? {
        val u = userDto.copy(id = users.size + 1)
        if (users.find { it.login == u.login } == null) {
            users.add(u)
            return u
        }
       return null
    }

    override fun deleteById(id: Int) {
        users.removeIf { it.id == id }
    }
}