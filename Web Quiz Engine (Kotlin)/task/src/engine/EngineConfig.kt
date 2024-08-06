package engine

import engine.api.Quiz
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EngineConfig {

    @Bean
    fun quizList(): List<Quiz> {
        return emptyList()
    }
}