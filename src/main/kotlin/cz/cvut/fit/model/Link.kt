package cz.cvut.fit.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.date
import java.time.LocalDate

data class LinkDto(
    val id: Int,
    val dest: String,
    val whole: String,
    val code: String,
    val created: LocalDate,
    val clicks: Int,
)

object Links : IntIdTable() {
    val dest = varchar("from", 400)
    val whole = varchar("to", 60).uniqueIndex()
    val code = varchar("code", 50).uniqueIndex()
    val created = date("date_created")
    val clicks = integer("clicks")
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
}

class Link(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Link>(Links)
    var dest by Links.dest
    var whole by Links.whole
    var code by Links.code
    var created by Links.created
    var clicks by Links.clicks
    var user by User referencedOn Links.user
    fun toDto() = LinkDto(id.value, dest, whole, code, created, clicks)
}