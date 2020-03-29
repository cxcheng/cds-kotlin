package sg.gov.tech.cds

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.InputStream

@RestController
class UserController(private val userRepo: UserRepository,
                     private val csvService: CsvService,
                     @Value("\${cds.default_min_salary}")
                     private val defaultMinSalary: Double,
                     @Value("\${cds.upload.success_url}")
                     private val uploadSuccessUrl: String,
                     @Value("\${cds.upload.failure_url}")
                     private val uploadFailureUrl: String) {

    private val logger = KotlinLogging.logger {}

    data class UserResults(val results: List<User>)

    @EventListener(ApplicationReadyEvent::class)
    internal fun seedDataIfEmpty() {
        // CHeck for empty DB by looking for at least one record
        if (userRepo.findFirstByOrderByNameAsc().isEmpty()) {
            logger.info("Seeding database from ${Constants.SEED_USERS_RESOURCE}")
            javaClass.getResource(Constants.SEED_USERS_RESOURCE)?.let {
                loadRepoFromCsv(it.openStream())
            } ?: run {
                logger.error("Unable to find ${Constants.SEED_USERS_RESOURCE}")
            }
        } else {
            logger.info("Database already has data, skipping seeding")
        }
    }

    @Transactional
    internal fun loadRepoFromCsv(csvIn: InputStream) {
        var numLoadedUsers = 0
        try {
            csvService.csvToUsers(csvIn).forEach {
                // we can do saveAll() to save entire list
                logger.info("Saving ${it.name}")
                userRepo.save(it)
                ++numLoadedUsers
            }
            logger.info("Successfully loaded ${numLoadedUsers} users")
        } catch (e: Exception) {
            logger.error("Error loading users from CSV", e)
        }
    }

    @GetMapping("/users")
    fun users(@RequestParam(required = false) min: Double?): UserResults {
        return UserResults(results =
        userRepo.findBySalaryGreaterThan(min ?: defaultMinSalary))
    }
/*
    @PostMapping("/upload")
    fun upload(@RequestParam("file") file: MultipartFile,
                  redirectAttributes: RedirectAttributes): String? {
        if (file.isEmpty) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload")
            return "redirect:${uploadFailureUrl}"
        }
        try {
            loadRepoFromCsv(ByteArrayInputStream(file.bytes))
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded ${file.originalFilename}")
        } catch (e: Exception) {
            logger.error("Unable to process uploaded file ${file.originalFilename}", e)
            return "redirect:${uploadFailureUrl}"
        }
        return "redirect:${uploadSuccessUrl}"
    }

 */
}
