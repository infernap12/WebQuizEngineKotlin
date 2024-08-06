package engine.api

import org.springframework.web.bind.annotation.ResponseBody

@ResponseBody
class GetQuizResponseDTO(
    val title: String,
    val text: String,
    val options: List<String>
)

@ResponseBody
class PostQuizResponseDTO(
    val success: Boolean,
    val feedback: String
)