package sg.gov.tech.cds

import com.opencsv.bean.CsvBindByPosition
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class User(
        @Id @CsvBindByPosition(position = 0) val name: String,
        @CsvBindByPosition(position = 1) val salary: Double)
