package engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size


class NewUserRequest(
    @field:Email()
    @JsonProperty("email")
    val email: String,
    @field:Size(min = 5)
    val password: String
)
