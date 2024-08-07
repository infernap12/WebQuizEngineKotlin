package engine.api

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MyRepository : JpaRepository<Quiz, Int>