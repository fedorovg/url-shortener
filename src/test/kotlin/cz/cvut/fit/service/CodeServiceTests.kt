package cz.cvut.fit.service

import cz.cvut.fit.ProjectConfig
import cz.cvut.fit.model.LinkDto
import cz.cvut.fit.model.Links
import cz.cvut.fit.service.ICodeService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldNotBe
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.instance
import java.time.LocalDate

class CodeServiceTests : StringSpec() {
    private val codeService by ProjectConfig.di.instance<ICodeService>()
    private val linkService by ProjectConfig.di.instance<ILinkService>()
    init {
        "Should generate a code for a string id pair" {
            val c = codeService.generate("https://google.com", 1)
            c shouldNotBe ""
        }
        "Should generate different codes for the same link and different users" {
            val c1 = codeService.generate("https://google.com", 1)
            val c2 = codeService.generate("https://google.com", 2)
            c1 shouldNotBe c2
        }
        "Should resort to a long code in case of a conflict" {
            val c1 = codeService.generate("https://google.com", 1)
            linkService.simpleAddLink(1, "https://google.com", c1) // Simulate a conflict by reusing the code
            val c2 = codeService.generate("https://google.com", 1)
            c1 shouldNotBe c2
            c2.length shouldBeGreaterThan c1.length
        }
    }
}