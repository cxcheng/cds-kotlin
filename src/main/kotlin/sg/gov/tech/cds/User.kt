package sg.gov.tech.cds

import com.opencsv.bean.CsvBindByName
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class User(
        @CsvBindByName val name: String,
        @CsvBindByName val salary: Double,
        @Id @GeneratedValue val id: Long? = null)
