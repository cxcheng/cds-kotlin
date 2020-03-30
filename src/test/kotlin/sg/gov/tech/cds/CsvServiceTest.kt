package sg.gov.tech.cds

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.isEmpty
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.io.ByteArrayInputStream
import java.io.InputStream

@ExtendWith(MockitoExtension::class)
class CsvServiceTest {

    private val logger = KotlinLogging.logger {}

    private val service = CsvService()

    private fun toInputStream(s: String): InputStream {
        return ByteArrayInputStream(s.toByteArray())
    }

    @Test
    fun `WHEN empty CSV to csvToUsers THEN empty list returned`() {
        logger.error("#### I was here")
        val usersList = service.csvToUsers(toInputStream(""))
        assertThat(usersList, isEmpty)
    }

    @Test
    fun `WHEN CSV with wrong number of columns THEN empty list returned`() {
        // 1-column only
        val input1 = CsvUtil.generateCSVColumns(10, 1)
        assertThat(service.csvToUsers(input1), isEmpty)
        // 3-columns
        val input3 = CsvUtil.generateCSVColumns(10, 3)
        assertThat(service.csvToUsers(input3), isEmpty)
    }

    @Test
    fun `WHEN malformed CSV to csvToUsers THEN empty list returned`() {
        // 1-column only
        // 3-columns
    }

    @Test
    fun `WHEN duplicate CSV to csvToUsers THEN all are returned in same order`() {
        /*
        doReturn("Hello service!").`when`(helloService).getHello()
        val result = helloController.helloService()
        assertNotNull(result)
        assertEquals("Hello service!", result)
         */
    }


}