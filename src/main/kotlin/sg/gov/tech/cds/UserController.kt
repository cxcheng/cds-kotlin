package sg.gov.tech.cds

import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.CsvToBeanBuilder
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

@RestController
class UserController(private val userRepo: UserRepository,
                     private val csvService: CSVService,
                     @Value("\${cds.default_min_salary}")
                     private val defaultMinSalary: Double) {

    private val logger = KotlinLogging.logger {}

    data class UserResults(val results: List<User>)

    @EventListener(ApplicationReadyEvent::class)
    fun seedDataIfEmpty() {
        // CHeck for empty DB by looking for at least one record
        if (userRepo.findFirstByOrderByNameAsc().isEmpty()) {
            logger.info("Seeding database from ${Constants.SEED_USERS_RESOURCE}")
            javaClass.getResource(Constants.SEED_USERS_RESOURCE)?.let {
                val numLoadedUsers = csvService.loadFromCSV(userRepo, it.openStream())
                logger.info("Successfully loaded ${numLoadedUsers} users")
            } ?: run {
                logger.error("Unable to find ${Constants.SEED_USERS_RESOURCE}")
            }
        } else {
            logger.info("Database already has data, skipping seeding")
        }
    }

    @GetMapping("/users")
    fun users(@RequestParam(required = false) min: Double?): UserResults {
        return UserResults(results =
            userRepo.findBySalaryGreaterThan(min ?: defaultMinSalary))
    }


}
