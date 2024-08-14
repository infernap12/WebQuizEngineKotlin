package engine.api

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Int> {
    fun findUserByEmail(username: String): User?
    fun existsByEmail(email: String): Boolean
}