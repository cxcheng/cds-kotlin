package sg.gov.tech.cds

import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class SeedDataLoader(private val userRepo: UserRepository,
                     private val csvService: CsvService) {

    private val logger = KotlinLogging.logger {}

    @EventListener(ApplicationReadyEvent::class)
    fun seedDataIfEmpty() {
        // CHeck for empty DB by looking for at least one record
        if (userRepo.isEmpty()) {
            logger.info("Seeding database from ${Constants.SEED_USERS_RESOURCE}")
            javaClass.getResource(Constants.SEED_USERS_RESOURCE)?.let {
                csvService.loadRepoFromCsv(userRepo, it.openStream())
            } ?: run {
                logger.error("Unable to find ${Constants.SEED_USERS_RESOURCE}")
            }
        } else {
            logger.info("Database already has data, skipping seeding")
        }
    }
}