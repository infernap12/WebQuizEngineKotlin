package engine.api

import org.springframework.web.bind.annotation.ResponseBody

@ResponseBody
class SolveQuizResponse(
    val success: Boolean,
    val feedback: String
)