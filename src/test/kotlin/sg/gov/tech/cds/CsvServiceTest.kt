package sg.gov.tech.cds

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CsvServiceTest {

    @Test
    fun `WHEN empty CSV to csvToUsers THEN empty list returned`() {

    }

    @Test
    fun `WHEN malformed CSV to csvToUsers THEN empty list returned`() {

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