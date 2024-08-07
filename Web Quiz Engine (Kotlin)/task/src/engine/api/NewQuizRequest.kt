package engine.api

class NewQuizRequest(
    val title: String,
    val text: String,
    val options: List<String>,
    val answer: Int
)