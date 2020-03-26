package sg.gov.tech.cds

import mu.KotlinLogging
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(private val userRepository: UserRepository) {

    private val logger = KotlinLogging.logger {}

    data class UserResults(val results: List<User>)

    @GetMapping("/users")
    fun users(model: Model): UserResults {
        return UserResults(results = userRepository.findBySalaryGreaterThan(4000.0))
    }

}
