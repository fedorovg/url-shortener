package cz.cvut.fit.service

import org.mindrot.jbcrypt.BCrypt

interface IPasswordService {
    fun hash(password: String): String
    fun compare(hash: String, toCheck: String): Boolean
}

class PasswordService : IPasswordService {
    override fun hash(password: String): String =
        // Salt is stored inside the hash itself
        BCrypt.hashpw(password, BCrypt.gensalt())

    override fun compare(hash: String, toCheck: String): Boolean =
        BCrypt.checkpw(toCheck, hash)
}