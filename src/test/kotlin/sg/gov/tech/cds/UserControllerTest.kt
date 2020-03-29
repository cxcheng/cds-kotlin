package sg.gov.tech.cds

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserControllerTest {

    //@InjectMocks
    //lateinit var userController: UserController

    @Test
    fun `WHEN users with no arguments THEN list of users gt defaultMinSalary returned`() {

    }

    @Test
    fun `WHEN users with min specified THEN list of users gt min returned`() {

    }

    @Test
    fun `WHEN users with malformed min THEN list of users gt defaultMinSalary returned`() {

    }

    @Test
    fun `WHEN upload with bad CSV THEN existing users gt defaultMinSalary returned`() {

    }

    @Test
    fun `WHEN upload with no existing users THEN existing and new users gt defaultMinSalary returned`() {
        /*
        doReturn("Hello service!").`when`(helloService).getHello()
        val result = helloController.helloService()
        assertNotNull(result)
        assertEquals("Hello service!", result)
         */
    }

    @Test
    fun `WHEN upload with existing users THEN union of updated users gt defaultMinSalary returned`() {
        /*
        doReturn("Hello service!").`when`(helloService).getHello()
        val result = helloController.helloService()
        assertNotNull(result)
        assertEquals("Hello service!", result)
         */
    }

}