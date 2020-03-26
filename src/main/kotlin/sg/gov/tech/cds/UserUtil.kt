package sg.gov.tech.cds

import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.CsvToBeanBuilder
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.Reader
import java.util.logging.Logger

@Component
class UserUtil {

     private val logger = KotlinLogging.logger {}

     fun loadFromCSV(csvIn: Reader): Boolean {
          logger.info("Loading from CSV")

          val mappingStrategy = ColumnPositionMappingStrategy<User>()
          mappingStrategy.setType(User::class.java)
          mappingStrategy.setColumnMapping("name", "salary")

          val r = BufferedReader(csvIn)
          with (r) {
               val csvToBean = CsvToBeanBuilder<User>(r)
                       .withMappingStrategy(mappingStrategy)
                       .withSkipLines(1)
                       .withIgnoreLeadingWhiteSpace(true)
                       .build()
               val users = csvToBean.parse()

          }

          return true
     }
}