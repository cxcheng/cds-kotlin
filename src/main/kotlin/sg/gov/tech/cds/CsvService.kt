package sg.gov.tech.cds

import com.opencsv.CSVWriter
import com.opencsv.bean.CsvToBeanBuilder
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.io.*

@Component
class CsvService {

    private val logger = KotlinLogging.logger {}

    fun csvToUsers(csvIn: InputStream): List<User> {
        try {
            // Read CSV file line-by-line, then parse into User each
            // "use" is similar to try with resources
            BufferedReader(InputStreamReader(csvIn)).use {
                return CsvToBeanBuilder<User>(it)
                        .withType(User::class.java)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build()
                        .parse()
            }
        } catch (e: Exception) {
            logger.error("Error reading CSV", e)
            return listOf()
        }
    }

    fun usersToCSV(out: OutputStream, users: List<User>): Int {
        var numLoadedUsers = 0
        try {
            val writer = CSVWriter(BufferedWriter(OutputStreamWriter(out)),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END)
            writer.use {
                // Write each User. No header
                users.forEach {
                    writer.writeNext(arrayOf(it.name, it.salary.toString()))
                    ++numLoadedUsers
                }
            }
        } catch (e: Exception) {
            logger.error("Error writing CSV", e)
        }
        return numLoadedUsers
    }
}