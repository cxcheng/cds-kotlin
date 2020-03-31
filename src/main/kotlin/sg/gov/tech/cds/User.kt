package sg.gov.tech.cds

import com.opencsv.bean.CsvBindByPosition
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class User(
        @Id @CsvBindByPosition(position = 0, required = true)
        val name: String,
        @CsvBindByPosition(position = 1, required = true)
        val salary: Double) {

    override operator fun equals(other: Any?): Boolean {
        if (other == null || other !is User) {
            return false
        }
        return other.name == name && other.salary == salary
    }
}
