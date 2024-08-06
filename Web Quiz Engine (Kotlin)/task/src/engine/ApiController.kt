package engine

import engine.api.GetQuizResponseDTO
import engine.api.PostQuizResponseDTO
import engine.api.Quiz
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController("/api/")
class ApiController {

    val memList: List<Quiz> = emptyList<Quiz>().toMutableList()


    // todo GET /api/quizzes/{id} to get a quiz by its id;
    @GetMapping("/quizzes/{id}")
    fun getQuiz(@PathVariable id: Int): GetQuizResponseDTO {
        return GetQuizResponseDTO(
            "The Java Logo",
            "What is depicted on the Java logo?",
            listOf("Robot", "Tea leaf", "Cup of coffee", "Bug")
        )
    }

    // todo POST /api/quizzes/{id}/solve?answer={index} to solve a specific quiz.
    @PostMapping("/quizzes/{id}/solve")
    fun solveQuiz(@RequestParam answer: Int, @PathVariable id: Int): PostQuizResponseDTO {
        return if (answer == 2) {
            PostQuizResponseDTO(true, "Congratulations, you're right!")
        } else {
            PostQuizResponseDTO(false, "Wrong answer! Please, try again.")
        }
    }

    // todo POST /api/quizzes to create a new quiz;
    @PostMapping("/quizzes")
    fun createQuiz() {
        TODO("create a new quiz")
    }

    // todo GET /api/quizzes to get all available quizzes; and
    @PostMapping("/quizzes")
    fun getAllQuiz() {
        TODO("get all quiz")
    }
}