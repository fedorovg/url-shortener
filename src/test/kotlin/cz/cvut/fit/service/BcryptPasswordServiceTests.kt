package cz.cvut.fit.service

import cz.cvut.fit.ProjectConfig
import cz.cvut.fit.service.IPasswordService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.kodein.di.instance

class BcryptPasswordServiceTests() : StringSpec() {
    private val passwordService by ProjectConfig.di.instance<IPasswordService>()
    init {
        "Provider should correctly compare password and it' hash" {
            val password = "loasdosdfsdlsdf"
            val hash = passwordService.hash(password)
            passwordService.compare(hash, password) shouldBe true
            passwordService.compare(hash, "asd2asdasd") shouldBe false
        }
    }
}