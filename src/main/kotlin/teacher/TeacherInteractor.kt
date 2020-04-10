package teacher

import discipline.Discipline
import java.sql.Connection
import java.sql.ResultSet

class TeacherInteractor(private val connection: Connection) {
    fun getAll(): List<Teacher> {
        val st = connection.createStatement()
        val res = st.executeQuery("SELECT id as id_teacher, name as teacher_name, surname, patronymic, phone_number, description FROM teacher")
        val ans = mutableListOf<Teacher>()
        while (res.next()) {
            ans.add(res.getTeacher())
        }
        return ans
    }

    companion object {
        fun ResultSet.getTeacher(): Teacher {
            val id = getInt("id_teacher")
            val name = getString("teacher_name")
            val surname = getString("surname")
            val patronymic = getString("patronymic")
            val phoneNumber = getString("phone_number")
            val description = getString("description")
            return Teacher(id, name, surname, patronymic, phoneNumber, description)
        }
    }
}