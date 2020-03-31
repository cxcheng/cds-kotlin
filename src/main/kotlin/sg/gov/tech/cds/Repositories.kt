package sg.gov.tech.cds

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
    fun findFirstByOrderByNameAsc(): List<User>
    fun findBySalaryGreaterThanEqual(salary: Double): List<User>
}
