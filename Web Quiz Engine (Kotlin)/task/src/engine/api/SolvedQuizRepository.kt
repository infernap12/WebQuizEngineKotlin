package engine.api

import engine.api.dto.SolvedQuiz
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SolvedQuizRepository : JpaRepository<SolvedQuiz, Int> {
    fun findAllByUserOrderBySolvedDateDesc(user: User, pageable: Pageable): Page<SolvedQuiz>
}