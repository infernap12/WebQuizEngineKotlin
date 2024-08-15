package engine.api.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

//use also for put
class NewQuizRequest(
    @field:NotEmpty
    val title: String,
    @field:NotEmpty
    val text: String,
    @field:Size(min = 2)
    val options: List<String>,
    val answer: List<Int>
)