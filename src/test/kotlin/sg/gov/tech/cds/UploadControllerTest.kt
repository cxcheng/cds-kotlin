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
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap
import java.io.ByteArrayOutputStream


@ExtendWith(MockitoExtension::class)
@DataJpaTest
@ActiveProfiles("test")
class UploadControllerTest {

    @Value("\${cds.default_min_salary}")
    private var defaultMinSalary = 0.0

    @Value("\${cds.upload.success_url}")
    private var successUrl = ""

    @Value("\${cds.upload.failure_url}")
    private var failureUrl = ""

    @Autowired
    private lateinit var userRepo: UserRepository

    // Test object with userRepo dependency
    private val csvService = CsvService()
    private lateinit var userController: UserController
    private lateinit var uploadController: UploadController
    private lateinit var defaultUserList: List<User>

    private fun createMultipartFile(bytes: ByteArray): MultipartFile =
            MockMultipartFile("dummy.csv", bytes)

    @BeforeEach
    fun setUp() {
        userController = UserController(userRepo, csvService, defaultMinSalary)
        uploadController = UploadController(userRepo, csvService, successUrl, failureUrl)

        // Setup database and defaultUserList with test_users seeded
        javaClass.getResource(TestConstants.TEST_USERS_RESOURCE)?.let {
            csvService.loadRepoFromCsv(userRepo, it.openStream())
            defaultUserList = csvService.csvToUsers(it.openStream()).sorted()
            // Double check that we have more than empty list
            assert(defaultUserList.size > 0)
        } ?: assert(false) { "Error loading ${TestConstants.TEST_USERS_RESOURCE}" }
    }

    @Test
    fun `WHEN upload with no existing users THEN error from upload, and database unchanged`() {
        val resp = uploadController.upload(createMultipartFile("".toByteArray()), RedirectAttributesModelMap())
        assert(resp.url == failureUrl)

        // Check load users the same with no changes in output
        val loadedUserList = userController.users(0.0).results
        assert(loadedUserList == defaultUserList.filter { it.salary >= 0.0 })
    }

    @Test
    fun `WHEN upload with some existing updated users THEN users on list updated, while users not on list unchanged`() {
        // We take every alternate user from the current DB and double the salary. Add a completely new one at the end.
        // THen we upload this list comprising half of the existing users with the one extra.
        val mergedUserList = mutableListOf<User>()
        val uploadUserList = mutableListOf<User>()
        var updateThisUser = true
        defaultUserList.forEach {
            if (updateThisUser) {
                val updatedUser = User(name = it.name, salary = it.salary * 2.0)
                mergedUserList += updatedUser
                uploadUserList += updatedUser
            } else {
                mergedUserList += it
            }
            updateThisUser = false
        }
        val extraUser = User(name = "Extra One", salary = 100000.0)
        uploadUserList += extraUser
        mergedUserList += extraUser

        // Upload to system the partial upload list and upload operation results in success redirect
        val output = ByteArrayOutputStream()
        csvService.usersToCSV(output, uploadUserList)
        val resp = uploadController.upload(createMultipartFile(output.toByteArray()), RedirectAttributesModelMap())
        assert(resp.url == successUrl)

        // Check users in the system are in the merged list
        val loadedUserList = userController.users(0.0).results
        assert(loadedUserList == mergedUserList.sorted().filter { it.salary >= 0.0 })
    }

    @Test
    fun `WHEN upload with bad CSV on some records THEN entire op should rollback with no partial updates`() {
        // Double salary of existing users, generate output
        val uploadUserList = mutableListOf<User>()
        defaultUserList.forEach {
            uploadUserList += User(name = it.name, salary = it.salary * 2.0)
        }
        val output = ByteArrayOutputStream()
        csvService.usersToCSV(output, uploadUserList)

        // Add one bad line
        output.write("This is a malformed line with one column only".toByteArray())

        // Upload, check for failure
        val resp = uploadController.upload(createMultipartFile(output.toByteArray()), RedirectAttributesModelMap())
        assert(resp.url == successUrl)

        // Check that database is unchanged
        val loadedUserList = userController.users(0.0).results
        assert(loadedUserList == defaultUserList.filter { it.salary >= 0.0 })
    }

}