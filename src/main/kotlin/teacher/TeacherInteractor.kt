package teacher

import discipline.Discipline
import org.slf4j.LoggerFactory
import java.lang.Exception
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

    fun add(teacher: Teacher): String? {
        val st = connection.prepareStatement("INSERT INTO teacher values (DEFAULT, ?, ?, ?, ?, ?)")
        st.setString(1, teacher.name)
        st.setString(2, teacher.surname)
        st.setString(3, teacher.patronymic)
        st.setString(4, teacher.phoneNumber)
        st.setString(5, teacher.description)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
    }

    fun delete(id: Int): String? {
        val st = connection.prepareStatement("DELETE FROM teacher WHERE id = ?")
        st.setInt(1, id)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
    }

    fun edit(teacher: Teacher): String? {
        val st = connection.prepareStatement("UPDATE teacher SET name = ?, surname = ?, patronymic = ?, phone_number = ?, description = ? WHERE id = ?")
        st.setString(1, teacher.name)
        st.setString(2, teacher.surname)
        st.setString(3, teacher.patronymic)
        st.setString(4, teacher.phoneNumber)
        st.setString(5, teacher.description)
        st.setInt(6, teacher.id)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
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