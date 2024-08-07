package engine

import com.fasterxml.jackson.databind.JsonMappingException
import engine.api.*
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
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
    fun solveQuiz(@RequestBody request: SolveRequest, @PathVariable id: Int): SolveQuizResponse {
        val quiz: Quiz = dao.findById(id).orElseThrow()
        return if (request.answer.toSet() == quiz.answer.toSet()) {
            SolveQuizResponse(true, "Congratulations, you're right!")
        } else {
            SolveQuizResponse(false, "Wrong answer! Please, try again.")
        }
    }

    // todo POST /api/quizzes to create a new quiz;
    @PostMapping("/api/quizzes")
    fun createQuiz(@Valid @RequestBody quizRequest: NewQuizRequest): Quiz {
        return dao.save(Quiz(quizRequest))
    }

    // todo GET /api/quizzes to get all available quizzes; and
    @GetMapping("/api/quizzes")
    fun getAllQuiz(): List<Quiz> {
        return dao.findAll()
    }

    @ExceptionHandler(QuizNotFoundException::class, NoSuchElementException::class)
    fun handleNotFoundException(): ResponseEntity<Void> {
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(MethodArgumentNotValidException::class, JsonMappingException::class)
    fun handleValidationException(): ResponseEntity<Void> {
        return ResponseEntity.badRequest().build()
    }
}