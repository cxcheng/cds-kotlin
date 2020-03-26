package sg.gov.tech.cds

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener


@SpringBootApplication
class CdsApplication {

	private val logger = KotlinLogging.logger {}

	@EventListener(ApplicationReadyEvent::class)
	fun loadCSV() {
		logger.info("hello world, I have just started up")
	}

}

fun main(args: Array<String>) {
	runApplication<CdsApplication>(*args)
}
