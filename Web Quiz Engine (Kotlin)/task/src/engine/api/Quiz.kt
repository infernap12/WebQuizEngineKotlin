package engine.api

data class Quiz(
    val title: String,
    val text: String,
    val options: List<String>,
    val answer: Int
)