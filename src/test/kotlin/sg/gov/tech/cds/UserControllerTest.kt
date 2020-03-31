package sg.gov.tech.cds

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@ExtendWith(MockitoExtension::class)
@DataJpaTest
class UserControllerTest {

    @Value("\${cds.default_min_salary}")
    private var defaultMinSalary: Double = 0.0

    @Autowired
    private lateinit var userRepo: UserRepository

    private val csvService = CsvService()

    // Test object with userRepo dependency
    private lateinit var userController: UserController
    private lateinit var defaultUserList: List<User>

    @BeforeEach
    fun setUp() {
        //MockitoAnnotations.initMocks(this)
        userController = UserController(userRepo, csvService, 4000.0, "success", "fail")
        userController.seedDataIfEmpty()

        // Setup defaultUserList for checking
        javaClass.getResource(Constants.SEED_USERS_RESOURCE)?.let {
            defaultUserList = csvService.csvToUsers(it.openStream())
            // Double check that we have more than empty list
            assert(defaultUserList.size > 0)
        } ?: assert(false) { "Error loading ${Constants.SEED_USERS_RESOURCE}" }
    }

    @Test
    fun `WHEN users with no arguments THEN list of users gt defaultMinSalary returned`() {
        val loadedUserList = userController.users(null).results
        assert(loadedUserList == defaultUserList.filter { it.salary >= defaultMinSalary })
    }

    @Test
    fun `WHEN users with min specified THEN list of users gt min returned`() {
        val loadedUserList = userController.users(100.0).results
        assert(loadedUserList == defaultUserList.filter { it.salary >= 100.0 })
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