package engine

import engine.api.MyRepository
import engine.api.NewQuizRequest
import engine.api.Quiz
import engine.api.SolveQuizResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController("/api/")
class ApiController {

    @Autowired
    lateinit var dao: MyRepository


    // todo GET /api/quizzes/{id} to get a quiz by its id;
    @GetMapping("/api/quizzes/{id}")
    fun getQuiz(@PathVariable id: Int): Quiz {
        val quiz = dao.findById(id).orElseThrow()
        return quiz
    }

    // todo POST /api/quizzes/{id}/solve?answer={index} to solve a specific quiz.
    @PostMapping("/api/quizzes/{id}/solve")
    fun solveQuiz(@RequestParam answer: Int, @PathVariable id: Int): SolveQuizResponse {
        val quiz: Quiz = dao.findById(id).orElseThrow()
        return if (answer == quiz.answer) {
            SolveQuizResponse(true, "Congratulations, you're right!")
        } else {
            SolveQuizResponse(false, "Wrong answer! Please, try again.")
        }
    }

    // todo POST /api/quizzes to create a new quiz;
    @PostMapping("/api/quizzes")
    fun createQuiz(@RequestBody quizRequest: NewQuizRequest): Quiz {
        return dao.save(Quiz(quizRequest))
    }

    // todo GET /api/quizzes to get all available quizzes; and
    @GetMapping("/api/quizzes")
    fun getAllQuiz(): List<Quiz> {
        return dao.findAll()
    }

    @ExceptionHandler(QuizNotFoundException::class, NoSuchElementException::class)
    fun handleValidationException(): ResponseEntity<Void> {
        return ResponseEntity.notFound().build()
    }
}