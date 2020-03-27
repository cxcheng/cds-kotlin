package sg.gov.tech.cds

import com.opencsv.bean.CsvBindByName
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class User(
        @Id @CsvBindByName val name: String,
        @CsvBindByName val salary: Double)
