package engine.api

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "USERS")
class User(
    @Id
    @GeneratedValue
    var id: Int? = null,

    @Email
    @Column(unique = true)
    var email: String,

    @Column
    private val password: String,

    @Transient
    @ElementCollection(fetch = FetchType.EAGER)
    private val authorities: MutableCollection<SimpleGrantedAuthority>? = mutableListOf(),

    // todo review correct cascadeType
    @OneToMany(mappedBy = "user")
    var quizzes: List<Quiz> = emptyList<Quiz>().toMutableList()
) : UserDetails {
    override fun getAuthorities(): MutableCollection<SimpleGrantedAuthority>? = authorities

    override fun getPassword(): String = password

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

}
