package engine.api.dto

import org.springframework.web.bind.annotation.ResponseBody

@ResponseBody
class SolveResponse(
    val success: Boolean,
    val feedback: String
)