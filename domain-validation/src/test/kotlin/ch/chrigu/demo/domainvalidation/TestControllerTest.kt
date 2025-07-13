package ch.chrigu.demo.domainvalidation

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TestControllerTest(private val webTestClient: WebTestClient) {
    @ParameterizedTest
    @CsvSource(
        """'{name: "abc"}', Invalid JSON: Unexpected character ('n' (code 110)): was expecting double-quote to start field name""",
        """'{"name": "Test", "age": 0}', Invalid domain object: Age must be 18 or greater""",
        """'{}', The required property name is missing""",
        """'{"name": "Test", "age": 18}', """
    )
    fun test(json: String, exception: String?) {
        val result = webTestClient.post().uri("/")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(json)
            .exchange()
            .expectStatus()
        if (exception == null) {
            result.isOk
        } else {
            result.isBadRequest
                .expectBody()
                .jsonPath("detail").isEqualTo(exception)
        }
    }
}
