package engine.api

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsImpl(private val repo: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(email: String): User {
        return repo.findUserByEmail(email) ?: throw UsernameNotFoundException("Not found")
    }
}