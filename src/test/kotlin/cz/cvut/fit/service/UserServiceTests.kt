package cz.cvut.fit.service

import cz.cvut.fit.ProjectConfig
import cz.cvut.fit.service.IUserService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.kodein.di.instance


class UserServiceTests : StringSpec() {
    private val userService by ProjectConfig.di.instance<IUserService>()

    init {
        "Should find inserted user by login" {
            userService.registerUser("log1", "123")
            userService.findByLogin("log1")?.login shouldBe "log1"
        }
        "Should fail to find a nonexistent user" {
            userService.findByLogin("asdasdassadjjhdfs") shouldBe null
        }
        "Should tell if user is present" {
            userService.doesUserExist("log1") shouldBe true
            userService.doesUserExist("asdasdassadjjhdfs") shouldBe false
        }

        "Should create a user with a unique login" {
            userService.registerUser("original", "kekes")
            userService.doesUserExist("original") shouldBe true
        }

        "Should fail to create a user with duplicate login" {
            userService.registerUser("unoriginal", "kekes")
            userService.registerUser("unoriginal", "kekes") shouldBe null
        }

    }
}