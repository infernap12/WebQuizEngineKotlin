package engine.api

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

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
    @Column(name = "ANSWER")
    val answer: Int
) {
    constructor(newQuizRequest: NewQuizRequest) : this(
        title = newQuizRequest.title,
        text = newQuizRequest.text,
        options = newQuizRequest.options,
        answer = newQuizRequest.answer
    )
}