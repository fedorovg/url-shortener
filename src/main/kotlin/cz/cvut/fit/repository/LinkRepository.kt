package cz.cvut.fit.repository

import cz.cvut.fit.model.Link
import cz.cvut.fit.model.LinkDto
import cz.cvut.fit.model.Links
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

interface ILinkRepository {
    fun findById(id: Int): LinkDto?
    fun findByWhole(whole: String): LinkDto?
    fun findByCode(code: String): LinkDto?
    fun findAllByUser(userId: Int): List<LinkDto>
    fun create(userId: Int, link: LinkDto): LinkDto?
    fun deleteLinkById(linkId: Int)
    fun deleteLinkByWhole(whole: String)
    fun updateLink(linkDto: LinkDto)
}

class LinkRepository : ILinkRepository {
    override fun findById(id: Int): LinkDto? = transaction {
        Link.findById(id)?.toDto()
    }

    override fun findByWhole(whole: String): LinkDto? = transaction {
        Link.find { Links.whole eq whole }.firstOrNull()?.toDto()
    }

    override fun findByCode(code: String): LinkDto? = transaction {
        Link.find { Links.code eq code }.firstOrNull()?.toDto()
    }

    override fun findAllByUser(userId: Int): List<LinkDto> = transaction {
        Link.find { Links.user eq userId }.map(Link::toDto)
    }

    override fun create(userId: Int, link: LinkDto): LinkDto? = kotlin.runCatching {
        transaction {
            val id = Links.insertAndGetId {
                it[dest] = link.dest
                it[whole] = link.whole
                it[code] = link.code
                it[created] = link.created
                it[clicks] = link.clicks
                it[user] = userId
            }.value
            return@transaction link.copy(id = id)
        }
    }.getOrNull()

    override fun deleteLinkById(linkId: Int) = transaction {
        val x = Link.find { Links.id eq linkId }.firstOrNull()?.delete()
    }

    override fun deleteLinkByWhole(whole: String) = transaction {
        val x = Link.find { Links.whole eq whole }.firstOrNull()?.delete()
    }

    override fun updateLink(linkDto: LinkDto) = transaction {
        val l = Link.findById( linkDto.id ) ?: return@transaction
        l.dest = linkDto.dest
        l.clicks = linkDto.clicks
        l.code = linkDto.code
        l.whole = linkDto.whole
    }
}

class ArrayLinkRepository : ILinkRepository {
    private data class MockLink(
        val id: Int,
        var dest: String,
        var whole: String,
        var code: String,
        val created: LocalDate,
        var clicks: Int,
        val userId: Int
    ) {
        fun toDto() = LinkDto(id, dest, whole, code, created, clicks)
    }

    private fun fromDto(dto: LinkDto, userId: Int) = MockLink(
        links.size + 1, dto.dest, dto.whole, dto.code, dto.created, dto.clicks, userId
    )

    private val links = mutableListOf<MockLink>()
    override fun findById(id: Int): LinkDto? = links.find { it.id == id }?.toDto()
    override fun findByWhole(whole: String): LinkDto? = links.find { it.whole == whole }?.toDto()
    override fun findByCode(code: String): LinkDto? = links.find { it.code == code }?.toDto()
    override fun findAllByUser(userId: Int): List<LinkDto> = links.filter { it.userId == userId }.map { it.toDto() }

    override fun create(userId: Int, link: LinkDto): LinkDto? {
        val l = fromDto(link, userId)
        if (links.find { it.whole == link.whole || it.code == link.code } == null) {
            links.add(l)
            return l.toDto()
        }
        return null
    }

    override fun deleteLinkById(linkId: Int) {
        links.removeIf { it.id == linkId }
    }

    override fun deleteLinkByWhole(whole: String) {
        links.removeIf { it.whole == whole }
    }

    override fun updateLink(linkDto: LinkDto) {
        links.find { it.id == linkDto.id }?.apply {
            this.clicks = linkDto.clicks
            this.code = linkDto.code
            this.dest = linkDto.dest
            this.whole = linkDto.whole
        }
    }
}