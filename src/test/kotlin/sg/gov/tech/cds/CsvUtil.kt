package sg.gov.tech.cds

import com.opencsv.CSVWriter
import java.io.*

class CsvUtil {

    companion object {
        fun generateRandomUsers(count: Int, minSalary: Int, maxSalary: Int): List<User> {
            val users = mutableListOf<User>()
            for (i in 1..count) {
                users.add(User(name = "Random User $i",
                        salary = (minSalary..maxSalary).random().toDouble()))
            }
            return users
        }

        fun generateCSVColumns(numRows: Int, numCols: Int, prefix: String = ""): InputStream {
            val out = ByteArrayOutputStream(8192)
            val writer = CSVWriter(BufferedWriter(OutputStreamWriter(out)),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END)
            writer.use {
                for (i in 1..numRows) {
                    val paramList = mutableListOf<String>()
                    repeat(numCols) {
                        paramList.add("$prefix$i")
                    }
                    writer.writeNext(paramList.toTypedArray())
                }
            }
            return ByteArrayInputStream(out.toByteArray())
        }
    }
}