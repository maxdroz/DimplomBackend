package office

import discipline.Discipline
import discipline.DisciplineInteractor.Companion.getDiscipline
import org.slf4j.LoggerFactory
import teacher.Teacher
import java.lang.Exception
import java.sql.Connection
import java.sql.ResultSet

class OfficeInteractor(private val connection: Connection) {
    fun getAll(): List<Office> {
        val st = connection.createStatement()
        val res = st.executeQuery("SELECT id as id_office, office FROM office")
        val ans = mutableListOf<Office>()
        while (res.next()) {
            ans.add(res.getOffice())
        }
        return ans
    }

    fun add(office: Office): String? {
        val st = connection.prepareStatement("INSERT INTO office VALUES (DEFAULT, ?)")
        st.setString(1, office.office)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
    }

    fun edit(office: Office): String? {
        val st = connection.prepareStatement("UPDATE office SET office = ? WHERE id = ?")
        st.setString(1, office.office)
        st.setInt(2, office.id)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
    }

    fun delete(id: Int): String? {
        val st = connection.prepareStatement("DELETE FROM office WHERE id = ?")
        st.setInt(1, id)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
    }

    companion object {
        fun ResultSet.getOffice(): Office {
            val id = getInt("id_office")
            val office = getString("office")
            return Office(id, office)
        }
    }
}