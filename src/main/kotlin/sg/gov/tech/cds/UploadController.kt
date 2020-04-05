package sg.gov.tech.cds

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.servlet.view.RedirectView
import java.io.ByteArrayInputStream

@Controller
class UploadController(private val userRepo: UserRepository,
                       private val csvService: CsvService,
                       @Value("\${cds.upload.success_url}")
                       private val uploadSuccessUrl: String,
                       @Value("\${cds.upload.failure_url}")
                       private val uploadFailureUrl: String) {

    private val logger = KotlinLogging.logger {}

    @PostMapping("/upload")
    fun upload(@RequestParam("file") file: MultipartFile,
               redirectAttributes: RedirectAttributes): RedirectView {
        if (file.isEmpty) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload")
            return RedirectView(uploadFailureUrl)
        }
        try {
            csvService.loadRepoFromCsv(userRepo, ByteArrayInputStream(file.bytes))
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded ${file.originalFilename}")
        } catch (e: Exception) {
            logger.error("Unable to process uploaded file ${file.originalFilename}", e)
            redirectAttributes.addFlashAttribute("message", "${e.message}")
            return RedirectView(uploadFailureUrl)
        }
        return RedirectView(uploadSuccessUrl)
    }

}