package engine

import com.fasterxml.jackson.databind.ObjectMapper
import engine.api.NewQuizRequest
import engine.api.Quiz
import engine.api.SolveRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ApiTestClient {

    @Autowired
    private lateinit var jacksonObjectMapper: ObjectMapper

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun postNewQuiz() {
        val request = NewQuizRequest(
            "The Java Logo",
            "What is depicted on the Java logo?",
            listOf("Robot", "Tea leaf", "Cup of coffee", "Bug"),
            listOf(2)
        )
        val expectedJson = """
        {
            "id": 1,
            "title": "The Java Logo",
            "text": "What is depicted on the Java logo?",
            "options": ["Robot", "Tea leaf", "Cup of coffee", "Bug"]
        }
        """.trimIndent()
        val expected = Quiz(1, request.title, request.text, request.options, request.answer)
        val mappedJson = jacksonObjectMapper.writeValueAsString(expected)
        webTestClient.post()
            .uri("/api/quizzes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk // should be isCreated, but Education test requires 200 OK
            .expectBody()
            .json(mappedJson)

        webTestClient.post().uri("/api/quizzes")
            .bodyValue(
                NewQuizRequest(
                    "The Ultimate Question",
                    "What is the answer to the Ultimate Question of Life, the Universe and Everything?",
                    listOf("Everything goes right", "42", "2+2=4", "11011100"),
                    listOf(1)
                )
            )
            .exchange()
    }

    @Test
    @DisplayName("Get existing quiz")
    fun example2() {
        val expectedJson = """
            {
              "id": 1,
              "title": "The Java Logo",
              "text": "What is depicted on the Java logo?",
              "options": ["Robot","Tea leaf","Cup of coffee","Bug"]
            }
        """.trimIndent()
        webTestClient.get()
            .uri("/api/quizzes/1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .json(expectedJson)
    }

    @Test
    @DisplayName("Get non-existing quiz")
    fun example3() {
        webTestClient.get()
            .uri("/api/quizzes/15")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    @DisplayName("Get all quiz")
    fun example4() {
        val expectedJson = """
            [
              {
                "id": 1,
                "title": "The Java Logo",
                "text": "What is depicted on the Java logo?",
                "options": ["Robot","Tea leaf","Cup of coffee","Bug"]
              },
              {
                "id": 2,
                "title": "The Ultimate Question",
                "text": "What is the answer to the Ultimate Question of Life, the Universe and Everything?",
                "options": ["Everything goes right","42","2+2=4","11011100"]
              }
            ]
        """.trimIndent()
        webTestClient.get()
            .uri("/api/quizzes")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .json(expectedJson)
    }

    @Test
    @DisplayName("Solve existing quiz")
    fun example5() {
        val request = SolveRequest(listOf(2))
        val expectedJson = """
            {
              "success": true,
              "feedback": "Congratulations, you're right!"
            }
        """.trimIndent()
        webTestClient.post()
            .uri("/api/quizzes/1/solve")
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .json(expectedJson)

    }

    @Test
    @DisplayName("Fail-Solve existing quiz")
    fun example6() {
        val request = SolveRequest(listOf(1))
        val expectedJson = """
            {
              "success": false,
              "feedback": "Wrong answer! Please, try again."
            }
        """.trimIndent()
        webTestClient.post()
            .uri("/api/quizzes/1/solve")
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .json(expectedJson)

    }

    @Test
    @DisplayName("Solve non-existing quiz")
    fun example7() {
        val request = SolveRequest(listOf(1))
        webTestClient.post()
            .uri("/api/quizzes/15/solve")
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
            .expectBody().isEmpty

    }

    @Test
    @DisplayName("Create quiz with missing title")
    fun example8() {
        val request = """
            {
              "text": "What is depicted on the Java logo?",
              "options": ["Robot","Tea leaf","Cup of coffee","Bug"],
              "answer": [2]
            }
        """.trimIndent()
        webTestClient.post()
            .uri("/api/quizzes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody().isEmpty
    }

    @Test
    @DisplayName("Create quiz with empty title")
    fun example9() {
        val request = """
            {
              "title": "",
              "text": "What is depicted on the Java logo?",
              "options": ["Robot","Tea leaf","Cup of coffee","Bug"],
              "answer": [2]
            }
        """.trimIndent()
        webTestClient.post()
            .uri("/api/quizzes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody().isEmpty
    }

    @Test
    @DisplayName("Create quiz with empty options")
    fun example10() {
        val request = """
            {
              "title": "The Java Logo",
              "text": "What is depicted on the Java logo?",
              "options": [],
              "answer": [2]
            }
        """.trimIndent()
        webTestClient.post()
            .uri("/api/quizzes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody().isEmpty
    }
}

