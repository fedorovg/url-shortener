package cz.cvut.fit.service

import cz.cvut.fit.model.Link
import cz.cvut.fit.model.LinkDto
import cz.cvut.fit.model.Links
import cz.cvut.fit.repository.ILinkRepository
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate


interface ILinkService {
    fun findById(id: Int): LinkDto?
    fun findByCode(code: String): LinkDto?
    fun findAllByUser(userId: Int): List<LinkDto>
    fun addLink(userId: Int, link: LinkDto): LinkDto?
    fun deleteLinkById(linkId: Int)
    fun simpleAddLink(userId: Int, from: String, code: String): LinkDto?
    fun updateLinkClicks(link: LinkDto)
}

class LinkService(
    private val baseUrl: String,
    private val linkRepository: ILinkRepository
    ) : ILinkService {

    override fun findById(id: Int): LinkDto? =
        linkRepository.findById(id)

    override fun findByCode(code: String): LinkDto? =
        linkRepository.findByCode(code)

    override fun findAllByUser(userId: Int): List<LinkDto> =
        linkRepository.findAllByUser(userId)

    override fun addLink(userId: Int, link: LinkDto): LinkDto? =
        linkRepository.create(userId, link)

    override fun deleteLinkById(linkId: Int) =
        linkRepository.deleteLinkById(linkId)

    override fun simpleAddLink(userId: Int, from: String, code: String): LinkDto? =
        linkRepository.create(
            userId,
            LinkDto(0, from, "$baseUrl/r/$code", code, LocalDate.now(), 0)
        )

    override fun updateLinkClicks(link: LinkDto) = transaction {
        linkRepository.updateLink(link.copy(clicks = link.clicks + 1))
    }
}