package engine

import com.fasterxml.jackson.databind.JsonMappingException
import engine.api.*
import engine.api.dto.NewQuizRequest
import engine.api.dto.NewUserRequest
import engine.api.dto.SolveResponse
import engine.api.dto.SolveRequest
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.web.exchanges.HttpExchange.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.net.http.HttpResponse

@RestController("/api/")
class ApiController {

    @Autowired
    lateinit var quizRepo: QuizRepository

    @Autowired
    lateinit var userRepo: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @DeleteMapping("/api/quizzes/{id}")
    fun deleteQuiz(@PathVariable id: Int, @AuthenticationPrincipal currentUser: UserDetails): HttpStatus {
        val quiz = quizRepo.findById(id).orElseThrow { QuizNotFoundException() }
        // todo review whether this is a proper impl
        if (quiz.user.email != currentUser.username) {
            return HttpStatus.FORBIDDEN
        }
        quizRepo.deleteById(id)
        return HttpStatus.NO_CONTENT
    }

    @PostMapping("/api/register")
    fun newUser(@RequestBody userReq: NewUserRequest): ResponseEntity<Void> {
        if (userRepo.existsByEmail(userReq.email)) {
            return ResponseEntity.badRequest().build()
        }
        val user = User(null, userReq.email, passwordEncoder.encode(userReq.password))
        userRepo.save(user)
        return ResponseEntity.ok().build()
    }

    /**
     * GET /api/quizzes/{id} to get a quiz by its id
     * */
    @GetMapping("/api/quizzes/{id}")
    fun getQuiz(@PathVariable id: Int): Quiz {
        val quiz = quizRepo.findById(id).orElseThrow { QuizNotFoundException() }
        return quiz
    }

    /**
     * POST /api/quizzes/{id}/solve?answer={index} to solve a specific quiz.
     * */
    @PostMapping("/api/quizzes/{id}/solve")
    fun solveQuiz(@RequestBody request: SolveRequest, @PathVariable id: Int): SolveResponse {
        val quiz: Quiz = quizRepo.findById(id).orElseThrow { QuizNotFoundException() }
        return if (request.answer.toSet() == quiz.answer.toSet()) {
            SolveResponse(true, "Congratulations, you're right!")
        } else {
            SolveResponse(false, "Wrong answer! Please, try again.")
        }
    }

    /**
     * POST /api/quizzes to create a new quiz
     */
    @PostMapping("/api/quizzes")
    fun createQuiz(
        @Valid @RequestBody quizRequest: NewQuizRequest,
        @AuthenticationPrincipal currentUser: UserDetails
    ): Quiz {
        val quiz = Quiz(
            -1,
            quizRequest.title,
            quizRequest.text,
            quizRequest.options,
            quizRequest.answer,
            userRepo.findUserByEmail(currentUser.username)!!,
        )

        return quizRepo.save(quiz)
    }

    /**
     * GET /api/quizzes to get all available quizzes
     */
    @GetMapping("/api/quizzes")
    fun getAllQuiz(): List<Quiz> {
        return quizRepo.findAll()
    }

    @ExceptionHandler(QuizNotFoundException::class)
    fun handleNotFoundException(): ResponseEntity<Void> {
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(MethodArgumentNotValidException::class, JsonMappingException::class)
    fun handleValidationException(): ResponseEntity<Void> {
        return ResponseEntity.badRequest().build()
    }
}