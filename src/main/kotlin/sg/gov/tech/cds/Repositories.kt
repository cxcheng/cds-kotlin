package sg.gov.tech.cds

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
    fun findFirstByOrderByNameAsc(): List<User>
    fun findBySalaryGreaterThanEqualOrderByNameAsc(salary: Double): List<User>
}

// Extend UserRepository
fun UserRepository.isEmpty() = this.findFirstByOrderByNameAsc().isEmpty()

