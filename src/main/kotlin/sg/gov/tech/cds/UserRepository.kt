package sg.gov.tech.cds

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
    fun findBySalaryGreaterThan(salary: Double): List<User>
}
