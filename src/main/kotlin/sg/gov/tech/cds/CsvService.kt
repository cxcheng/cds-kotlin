package sg.gov.tech.cds

import com.opencsv.CSVWriter
import com.opencsv.bean.CsvToBeanBuilder
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.*

@Component
class CsvService {

    private val logger = KotlinLogging.logger {}

    fun csvToUsers(csvIn: InputStream): List<User> {
        // Read CSV file line-by-line, then parse into User each
        // "use" is similar to try with resources
        BufferedReader(InputStreamReader(csvIn)).use {
            var userList: List<User>
            try {
                userList = CsvToBeanBuilder<User>(it)
                        .withType(User::class.java)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build()
                        .parse()
            } catch (e: Exception) {
                logger.error("Error parsing CSV, returning empty list: ${e.message}")
                userList = listOf()
            }
            return userList
        }
    }

    fun usersToCSV(out: OutputStream, users: List<User>) {
        val writer = CSVWriter(BufferedWriter(OutputStreamWriter(out)),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)
        writer.use {
            // Write each User. No header
            try {
                users.forEach {
                    writer.writeNext(arrayOf(it.name, it.salary.toString()))
                }
            } catch (e: Exception) {
                logger.error("Error writing CSV", e)
            }
        }
    }

    @Transactional
    fun loadRepoFromCsv(userRepo: UserRepository, csvIn: InputStream) {
        var numLoadedUsers = 0
        try {
            csvToUsers(csvIn).forEach {
                userRepo.save(it)
                ++numLoadedUsers
            }
            logger.info("Successfully loaded ${numLoadedUsers} users")
        } catch (e: Exception) {
            logger.error("Error loading users from CSV", e)
        }
    }

}