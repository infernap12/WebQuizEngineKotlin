package engine.api.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import engine.api.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "solved_quizzes")
class SolvedQuiz(
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val solvedQuizId: Int? = null,

    @ManyToOne
    @JsonIgnore
    @JoinColumn(referencedColumnName = "id")
    val user: User,

    @Column(name = "quiz_Id")
    @JsonProperty("id")
    val quizId: Int,

    @Column(name = "completed_at")
    @JsonProperty("completedAt")
    val solvedDate: LocalDateTime = LocalDateTime.now()
)