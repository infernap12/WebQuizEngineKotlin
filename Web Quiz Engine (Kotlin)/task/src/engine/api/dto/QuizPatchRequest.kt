package engine.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

class QuizPatchRequest(
    @field:NotEmpty
    @JsonProperty(required = false)
    val title: String? = null,

    @field:NotEmpty
    @JsonProperty(required = false)
    val text: String? = null,

    @field:Size(min = 2)
    @JsonProperty(required = false)
    val options: List<String>? = null,

    @JsonProperty(required = false)
    val answer: List<Int>? = null
)