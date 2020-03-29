package sg.gov.tech.cds

class CSVUtil {

    fun generateTestUsers(count: Int, minSalary: Int, maxSalary: Int): List<User> {
        var users = mutableListOf<User>()
        for (i in 0..count) {
            users.add(User(name="Random User ${i}",
                    salary=(minSalary..maxSalary).random().toDouble()))
        }
        return users
    }

    fun generateCSV(userList: List<User>): String {
        return ""
    }
}