package cz.cvut.fit

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.ktor.http.*
import io.ktor.server.testing.*


class IntegrationTest : StringSpec() {
    private val json = ObjectMapper()

    private fun TestApplicationEngine.registerUser(login: String, password: String): TestApplicationCall =
        handleRequest(HttpMethod.Post, "/api/v1/register") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            val body = json.writeValueAsString(
                mapOf(
                    "login" to login,
                    "password" to password
                )
            )
            setBody(body)
        }

    private fun TestApplicationEngine.loginUser(login: String, password: String): TestApplicationCall =
        handleRequest(HttpMethod.Post, "/api/v1/login") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                json.writeValueAsString(
                    mapOf(
                        "login" to login,
                        "password" to password
                    )
                )
            )
        }

    private fun TestApplicationEngine.getLinks(token: String = "", linkId: Int = -1): TestApplicationCall =
        handleRequest(
            HttpMethod.Get,
            if (linkId > -1) {
                "/api/v1/link/$linkId"
            } else {
                "/api/v1/link"
            }
        ) {
            addHeader(HttpHeaders.Authorization, "Bearer $token")
        }

    private fun TestApplicationEngine.createLink(token: String = "", from: String): TestApplicationCall =
        handleRequest(HttpMethod.Post, "/api/v1/link") {
            addHeader(HttpHeaders.Authorization, "Bearer $token")
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                json.writeValueAsString(
                    mapOf(
                        "from" to from
                    )
                )
            )
        }

    private fun TestApplicationEngine.redirect(code: String): TestApplicationCall =
        handleRequest(HttpMethod.Get, "/r/$code")

    init {
        "Integration tests" {
            withTestApplication({ module(testing = true) }) {
                registerUser("testLogin", "testPassword").apply {
                    response.shouldHaveStatus(HttpStatusCode.Created)
                }

                loginUser("wrongLogin", "incorrectPassword").apply {
                    response.shouldHaveStatus(HttpStatusCode.BadRequest)
                }

                val token = loginUser("testLogin", "testPassword").run {
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.content shouldContain "token"
                    val tok = json.readValue<HashMap<String, String>>(response.content!!)["token"]
                    tok shouldNotBe null
                    tok
                }!!

                getLinks().apply { // No token
                    response.shouldHaveStatus(HttpStatusCode.Unauthorized)
                }

                createLink(token, "https://google.com").apply {
                    response.shouldHaveStatus(HttpStatusCode.Created)
                }

                createLink(token, "https://bing.com").apply {
                    response.shouldHaveStatus(HttpStatusCode.Created)
                }

                val code = getLinks(token).run {
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    val links = json.readValue<Array<HashMap<String, String>>>(response.content!!)
                    links.size shouldBe 2
                    links[0]["code"]!! // If this is null, the whole test should fail
                }

                getLinks(token, 1).apply {
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }

                redirect(code).apply {
                    response.shouldHaveStatus(301)
                }
                getLinks(token).apply {
                    val links = json.readValue<Array<HashMap<String, String>>>(response.content!!)
                    links[0]["clicks"]?.toInt()!! shouldBeGreaterThan 0
                }
            }
        }
    }
}