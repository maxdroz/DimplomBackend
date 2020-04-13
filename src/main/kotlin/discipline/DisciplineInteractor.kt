package discipline

import office.Office
import office.OfficeInteractor.Companion.getOffice
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.sql.Connection
import java.sql.ResultSet

class DisciplineInteractor(private val connection: Connection) {
    fun getAll(): List<Discipline> {
        val st = connection.createStatement()
        val res = st.executeQuery("SELECT id as id_discipline, name FROM discipline")
        val ans = mutableListOf<Discipline>()
        while(res.next()){
            ans.add(res.getDiscipline())
        }
        return ans
    }

    fun get(id: Int): Discipline {
        val st = connection.prepareStatement("SELECT id as id_discipline, name FROM discipline WHERE id = ?")
        st.setInt(1, id)
        val res = st.executeQuery()
        res.next()
        return res.getDiscipline()
    }

    fun add(discipline: Discipline): String? {
        val st = connection.prepareStatement("INSERT INTO discipline VALUES (DEFAULT, ?)")
        st.setString(1, discipline.name)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
    }

    fun edit(discipline: Discipline): String? {
        val st = connection.prepareStatement("UPDATE discipline SET name = ? WHERE id = ?")
        st.setString(1, discipline.name)
        st.setInt(2, discipline.id)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
    }

    fun delete(id: Int): String? {
        val st = connection.prepareStatement("DELETE FROM discipline WHERE id = ?")
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
        fun ResultSet.getDiscipline(): Discipline {
            val id = getInt("id_discipline")
            val name = getString("name")
            return Discipline(id, name)
        }
    }
}