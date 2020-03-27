package sg.gov.tech.cds

import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.CsvToBeanBuilder
import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

@RestController
class UserController(private val userRepository: UserRepository) {

    private val logger = KotlinLogging.logger {}

    data class UserResults(val results: List<User>)

    @EventListener(ApplicationReadyEvent::class)
    fun seedDataIfEmpty() {
        // CHeck for empty DB by looking for 1
        val results = userRepository.findFirstByOrderByNameAsc()
        if (results.isEmpty()) {
            logger.info("Seeding database")
            javaClass.getResource("/users.csv")?.let {
                loadFromCSV(it.openStream())
            } ?: run {
                logger.error("Unable to find /users.csv")
            }
        } else {
            logger.info("Database already has data, skipping seeding")
        }
    }

    @GetMapping("/users")
    fun users(@RequestParam(required = false) min: Double?): UserResults {
        return UserResults(results = min?.let {
            userRepository.findBySalaryGreaterThan(min)
        } ?: listOf())
    }

    fun loadFromCSV(csvIn: InputStream): Boolean {
        val mappingStrategy = ColumnPositionMappingStrategy<User>()
        mappingStrategy.type = User::class.java
        mappingStrategy.setColumnMapping("name", "salary")

        try {
            BufferedReader(InputStreamReader(csvIn)).use {
                val csvToBean = CsvToBeanBuilder<User>(it)
                        .withMappingStrategy(mappingStrategy)
                        .withSkipLines(1)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build()
                csvToBean.parse().forEach {
                    logger.info("Saving ${it.name}")
                    userRepository.save(it)
                }
            }
            return true
        } catch (e: Exception) {
            logger.error("Error loading CSV", e)
            return false
        }
    }

}
