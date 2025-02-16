package engine.api

import com.fasterxml.jackson.annotation.JsonIgnore
import engine.api.dto.NewQuizRequest
import jakarta.persistence.*
import java.awt.SystemColor.text

@Entity
@Table(name = "QUIZZES")
data class Quiz(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_ID", nullable = false)
    val id: Int = -1,

    @Column(name = "TITLE", nullable = false)
    val title: String,

    @Column(name = "TEXT", nullable = false)
    val text: String,

    @ElementCollection
    @Column(name = "ANSWER_OPTIONS")
    val options: List<String>,

    @JsonIgnore
    @ElementCollection()
    @Column(name = "ANSWER")
    val answer: List<Int>,

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    val user: User
) {
    /*constructor(newQuizRequest: NewQuizRequest) : this(
        title = newQuizRequest.title,
        text = newQuizRequest.text,
        options = newQuizRequest.options,
        answer = newQuizRequest.answer
    )*/
}