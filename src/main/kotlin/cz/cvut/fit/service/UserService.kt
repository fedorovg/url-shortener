package cz.cvut.fit.service

import cz.cvut.fit.model.UserDto
import cz.cvut.fit.repository.IUserRepository

interface IUserService {
    fun doesUserExist(login: String): Boolean
    fun findById(id: Int): UserDto?
    fun findByLogin(login: String): UserDto?
    fun registerUser(login: String, password: String): UserDto?
    fun compare(hash: String, toCheck: String): Boolean
}

class UserService(
    private val userRepository: IUserRepository,
    private val passwordService: IPasswordService
) : IUserService {
    override fun doesUserExist(login: String): Boolean =
        userRepository.findByLogin(login) != null

    override fun findById(id: Int): UserDto? =
        userRepository.findById(id)

    override fun findByLogin(login: String): UserDto? =
        userRepository.findByLogin(login)

    override fun compare(hash: String, toCheck: String): Boolean =
        passwordService.compare(hash, toCheck)

    override fun registerUser(login: String, password: String): UserDto? =
        userRepository.create(
            UserDto(
                id = 0,
                login = login,
                password = passwordService.hash(password)
            )
        )
}