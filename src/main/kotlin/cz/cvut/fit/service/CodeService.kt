package cz.cvut.fit.service

import org.hashids.Hashids
import java.math.BigInteger
import kotlin.math.absoluteValue

interface ICodeService {
    fun generate(from: String, userId: Int): String
}

class CodeService(private val linkService: ILinkService) : ICodeService {
    companion object {
        const val SALT = "MY_COOL_SALT"
    }

    private val hashids = Hashids(SALT, 5)

    private fun generateLongCode(from: String): String =
        toHex(from).let { hashids.encodeHex(it) }

    private fun generateCode(number: Int): String =
        hashids.encode(number.toLong())

    private fun toHex(arg: String): String =
        String.format("%040x", BigInteger(1, arg.toByteArray()))

    override fun generate(from: String, userId: Int): String {
        val hash = "$from$userId".hashCode().absoluteValue
        val c = generateCode(hash)
        return if (linkService.findByCode(c) != null) {
            generateLongCode("$from$userId")
        } else {
            c
        }
    }
}
