package engine.api

import jakarta.persistence.*
import jakarta.validation.constraints.Email

@Entity
@Table(name = "USERS")
class User(
    @Id
    @GeneratedValue
    var id: Int? = null,

    @Email
    var email: String? = null,

    var password: String? = null,
    var authority: String? = "ROLE_USER",

    // todo review correct cascadeType
    @OneToMany(mappedBy = "user")
    var quizzes: List<Quiz> = emptyList<Quiz>().toMutableList()
)
