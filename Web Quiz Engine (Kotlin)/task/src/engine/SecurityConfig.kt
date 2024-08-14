package engine

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableWebSecurity
class SecurityConfig {

    /*@Bean
    fun userDetailsService(): UserDetailsService {
        val user1 = User.withUsername("user1")
            .password(passwordEncoder().encode("pass1"))
            .authorities("WRITE")
            .build()
        val user2 = User.withUsername("user2")
            .password(passwordEncoder().encode("pass2"))
            .roles("USER")
            .build()
        val user3 = User.withUsername("user3")
            .password(passwordEncoder().encode("pass3"))
            .authorities("ROLE_ADMIN", "WRITE")
            .build()

        return InMemoryUserDetailsManager(user1, user2, user3)
    }*/


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic(Customizer.withDefaults())
            .csrf {
                it.disable()
                it.ignoringRequestMatchers(AntPathRequestMatcher("/h2-console/**"))
            }  // for POST requests via Postman
            .headers { headers ->
                headers.frameOptions { frameOptions ->
                    frameOptions.sameOrigin()
                }
            }
            .authorizeHttpRequests { matcherRegistry ->
                matcherRegistry
                    .requestMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/register").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .anyRequest().denyAll()
            }
            .securityContext { it.requireExplicitSave(false) }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}

