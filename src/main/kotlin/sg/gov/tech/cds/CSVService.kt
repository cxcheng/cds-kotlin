package sg.gov.tech.cds

import com.opencsv.CSVWriter
import com.opencsv.bean.CsvToBeanBuilder
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.*

@Component
class CSVService {

    private val logger = KotlinLogging.logger {}

    @Transactional
    fun loadFromCSV(userRepo: UserRepository, csvIn: InputStream): Int {
        var numLoadedUsers = 0
        try {
            BufferedReader(InputStreamReader(csvIn)).use {
                val csvToBean = CsvToBeanBuilder<User>(it)
                        .withType(User::class.java)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build()
                csvToBean.parse().forEach {
                    logger.info("Saving ${it.name}")
                    userRepo.save(it)
                    ++numLoadedUsers
                }
            }
        } catch (e: Exception) {
            logger.error("Error reading CSV", e)
        }
        return numLoadedUsers
    }

    fun writeToCSV(out: OutputStream, users: List<User>): Int {
        var numLoadedUsers = 0
        try {
            var writer = CSVWriter(BufferedWriter(OutputStreamWriter(out)),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END)
            writer.use {
                users.forEach {
                    val user = it
                    writer.writeNext(arrayOf(user.name, user.salary.toString()))
                    ++numLoadedUsers
                }
            }

        } catch (e: Exception) {
            logger.error("Error writing CSV", e)
        }
        return numLoadedUsers
    }
}