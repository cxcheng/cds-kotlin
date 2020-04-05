package sg.gov.tech.cds

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

typealias Double1 = Double

@RestController
class UserController(private val userRepo: UserRepository,
                     private val csvService: CsvService,
                     @Value("\${cds.default_min_salary}")
                     private val defaultMinSalary: Double1) {

    private val logger = KotlinLogging.logger {}

    data class UserResults(val results: List<User>)

    @GetMapping("/users")
    fun users(@RequestParam(required = false) min: Double?): UserResults {
        return UserResults(results =
        userRepo.findBySalaryGreaterThanEqualOrderByNameAsc(min ?: defaultMinSalary))
    }

}
