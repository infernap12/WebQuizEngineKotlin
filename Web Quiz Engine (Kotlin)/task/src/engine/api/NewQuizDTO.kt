package engine.api

class NewQuizDTO(
    val title: String,
    val text: String,
    val options: List<String>,
    val answer: Int
)