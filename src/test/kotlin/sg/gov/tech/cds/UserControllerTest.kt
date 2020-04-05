package sg.gov.tech.cds

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.multipart.MultipartFile

@ExtendWith(MockitoExtension::class)
@DataJpaTest
@ActiveProfiles("test")
class UserControllerTest {

    @Value("\${cds.default_min_salary}")
    private var defaultMinSalary = 0.0

    @Autowired
    private lateinit var userRepo: UserRepository


    // Test object with userRepo dependency
    private val csvService = CsvService()
    private lateinit var userController: UserController
    private lateinit var defaultUserList: List<User>

    private fun createMultipartFile(bytes: ByteArray): MultipartFile =
            MockMultipartFile("dummy.csv", bytes)

    @BeforeEach
    fun setUp() {
        userController = UserController(userRepo, csvService, defaultMinSalary)

        // Setup database and defaultUserList with test_users seeded
        javaClass.getResource(TestConstants.TEST_USERS_RESOURCE)?.let {
            csvService.loadRepoFromCsv(userRepo, it.openStream())
            defaultUserList = csvService.csvToUsers(it.openStream()).sorted()
            // Double check that we have more than empty list
            assert(defaultUserList.size > 0)
        } ?: assert(false) { "Error loading ${TestConstants.TEST_USERS_RESOURCE}" }
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

}