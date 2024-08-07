package engine.api

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size


class NewQuizRequest(
    @field:NotEmpty
    val title: String,
    @field:NotEmpty
    val text: String,
    @field:Size(min = 2)
    val options: List<String>,
    val answer: List<Int>
)