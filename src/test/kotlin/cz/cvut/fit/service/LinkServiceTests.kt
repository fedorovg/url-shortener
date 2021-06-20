package cz.cvut.fit.service

import cz.cvut.fit.ProjectConfig
import cz.cvut.fit.model.LinkDto
import cz.cvut.fit.service.ILinkService
import cz.cvut.fit.service.IUserService
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.kodein.di.instance
import java.time.LocalDate

class LinkServiceTests : StringSpec() {
    private val linkService by ProjectConfig.di.instance<ILinkService>()
    private val userService by ProjectConfig.di.instance<IUserService>()

    init {
        "Should create Links" {
            val link = LinkDto(0, "a", "b1", "CDT1", LocalDate.now(), 1)
            val added = linkService.addLink(11, link) ?: fail("Couldn't find created link.")
            linkService.findById(added.id)?.id shouldBe added.id
        }
        "Should fail on unique constraint violations" {
            val link = LinkDto(0, "a", "b2", "CDT2", LocalDate.now(), 1)
            linkService.addLink(11, link)
            val id = linkService.addLink(1, link)
            id shouldBe null
        }

        "Should get links by user" {
            val userId = userService.registerUser("testUser", "testPassword")!!.id
            val link1 = LinkDto(0, "abc", "def3", "CDT3", LocalDate.now(), 1)
            linkService.addLink(userId, link1)
            val link2 = LinkDto(0, "lol", "kek4", "CDTA4", LocalDate.now(), 1)
            linkService.addLink(userId, link2)
            val found = linkService.findAllByUser(userId).size
            found shouldBe 2
        }

    }
}