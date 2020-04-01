package sg.gov.tech.cds

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.isEmpty
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@ExtendWith(MockitoExtension::class)
class CsvServiceTest {

    private val logger = KotlinLogging.logger {}

    private val service = CsvService()

    @Test
    fun `WHEN empty CSV to csvToUsers THEN empty list returned`() {
        assertThat(service.csvToUsers(ByteArrayInputStream("".toByteArray())), isEmpty)
    }

    @Test
    fun `WHEN CSV with not enough columns THEN empty list returned`() {
        // 1-column only
        val input1 = CsvUtil.generateCSVColumns(10, 1)
        assertThat(service.csvToUsers(input1), isEmpty)
    }

    @Test
    fun `WHEN CSV with more columns THEN full list returned`() {
        // 3-columns
        val input3 = CsvUtil.generateCSVColumns(10, 3)
        assertThat(service.csvToUsers(input3), hasSize(equalTo(10)))
    }

    @Test
    fun `WHEN CSV with nalformed salary THEN empty list returned`() {
        val input3 = CsvUtil.generateCSVColumns(10, 2, "not number")
        assertThat(service.csvToUsers(input3), isEmpty)
    }

    @Test
    fun `WHEN duplicate CSV to csvToUsers THEN all are returned in same order`() {
        // Generate the test data
        val userList = CsvUtil.generateRandomUsers(10, 0, 10000)
        val duplicatedUserList = mutableListOf<User>()
        duplicatedUserList.addAll(userList)
        duplicatedUserList.addAll(userList)
        assertThat(duplicatedUserList, hasSize(equalTo(20)))

        // Import test data
        val out = ByteArrayOutputStream()
        service.usersToCSV(out, duplicatedUserList)
        val importedUsers = service.csvToUsers(ByteArrayInputStream(out.toByteArray()))

        // Validate imported data. Should match original list
        // Need to override equals operator for structural compare
        assert(importedUsers == duplicatedUserList)
    }

    @Test
    fun `WHEN converting input to CSV THEN CSV can be loaded again`() {
        val userList = service.csvToUsers(CsvUtil.generateCSVColumns(10, 2))
        val output = ByteArrayOutputStream()
        service.usersToCSV(output, userList)
        val importedList = service.csvToUsers(ByteArrayInputStream(output.toByteArray()))
        assert(importedList == userList)
    }

}