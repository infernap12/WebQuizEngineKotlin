package engine

import com.fasterxml.jackson.databind.JsonMappingException
import engine.api.*
import engine.api.dto.*
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.web.exchanges.HttpExchange.Response
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.awt.SystemColor.text
import java.net.URI
import java.net.http.HttpResponse
import java.util.logging.Logger
import kotlin.jvm.optionals.getOrNull

@RestController("/api/")
class ApiController {

    @Autowired
    lateinit var quizRepo: QuizRepository

    @Autowired
    lateinit var userRepo: UserRepository

    @Autowired
    lateinit var solvedQuizRepo: SolvedQuizRepository

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

    @PutMapping("/api/quizzes/{id}")
    fun putQuiz(
        @PathVariable id: Int,
        @RequestBody quizRequest: NewQuizRequest,
        @AuthenticationPrincipal currentUser: UserDetails
    ): ResponseEntity<Quiz?> {
        val quiz = quizRepo.findById(id).getOrNull()
        if (quiz == null) {
            val newQuiz = Quiz(
                title = quizRequest.title,
                text = quizRequest.text,
                options = quizRequest.options,
                answer = quizRequest.answer,
                user = userRepo.findUserByEmail(currentUser.username)!!
            )
            val saved = quizRepo.save(newQuiz)
            /*"/api/quizzes/$id"*/
            return ResponseEntity.created(URI.create("/api/quizzes/${saved.id}")).body(saved)
        } else {
            if (quiz.user.email != currentUser.username) {
                return ResponseEntity.status(403).build()
            } else {
                val updatedQuiz =
                    Quiz(id, quizRequest.title, quizRequest.text, quizRequest.options, quizRequest.answer, quiz.user)
                val saved = quizRepo.save(updatedQuiz)
                return ResponseEntity.ok(saved)
            }
        }
    }

    @PatchMapping("/api/quizzes/{id}")
    fun patchQuiz(
        @PathVariable id: Int,
        @RequestBody req: QuizPatchRequest,
        @AuthenticationPrincipal currentUser: UserDetails
    ): ResponseEntity<Void> {
        val quiz = quizRepo.findById(id).orElseThrow { QuizNotFoundException() }
        if (quiz.user.email != currentUser.username) {
            return ResponseEntity.status(403).build()
        }

        val patchedQuiz = Quiz(
            id = id,
            title = req.title ?: quiz.title,
            text = req.text ?: quiz.text,
            options = req.options ?: quiz.options,
            answer = req.answer ?: quiz.answer,
            user = quiz.user,
        )
        quizRepo.save(patchedQuiz)
        return ResponseEntity.noContent().build()

    }

    @PostMapping("/api/register")
    fun newUser(@Validated @RequestBody userReq: NewUserRequest): ResponseEntity<Void> {
        if (userRepo.existsByEmail(userReq.email)) {
            return ResponseEntity.badRequest().build()
        }
        // dumb hyperskill not-real requirement for real address
        // if an address from a tld wants to use the service, let them
        if ('.' !in userReq.email.split('@')[1]) {
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

    @GetMapping("/api/quizzes/completed")
    fun getCompleted(@RequestParam("page") page: Int, @AuthenticationPrincipal user: User): Page<SolvedQuiz> {
        return solvedQuizRepo.findAllByUserOrderBySolvedDateDesc(user, PageRequest.of(page, 10))
    }

    /**
     * POST /api/quizzes/{id}/solve?answer={index} to solve a specific quiz.
     * */
    @PostMapping("/api/quizzes/{id}/solve")
    fun solveQuiz(
        @RequestBody request: SolveRequest,
        @PathVariable id: Int,
        @AuthenticationPrincipal user: User
    ): SolveResponse {
        val quiz: Quiz = quizRepo.findById(id).orElseThrow { QuizNotFoundException() }
        return if (request.answer.toSet() == quiz.answer.toSet()) {
            solvedQuizRepo.save(SolvedQuiz(quizId = id, user = user))
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
    fun getAllQuiz(@RequestParam("page") page: Int): Page<Quiz> {
        return quizRepo.findAll(PageRequest.of(page, 10))
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