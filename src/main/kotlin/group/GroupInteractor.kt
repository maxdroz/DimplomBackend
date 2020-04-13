package group

import office.Office
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.sql.Connection
import java.sql.ResultSet

class GroupInteractor(private val connection: Connection) {
    fun getAll(): List<Group> {
        val st = connection.createStatement()
        val res = st.executeQuery("SELECT id as id_group, name as group_name FROM \"group\"")
        val ans = mutableListOf<Group>()
        while (res.next()) {
            ans.add(res.getGroup())
        }
        return ans
    }

    fun get(id: Int): Group {
        val st = connection.prepareStatement("SELECT id as id_group, name as group_name FROM \"group\" WHERE id = ?")
        st.setInt(1, id)
        val res = st.executeQuery()
        res.next()
        return res.getGroup()
    }

    fun add(group: Group): String? {
        val st = connection.prepareStatement("INSERT INTO \"group\" VALUES (DEFAULT, ?)")
        st.setString(1, group.name)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
    }

    fun edit(group: Group): String? {
        val st = connection.prepareStatement("UPDATE \"group\" SET name = ? WHERE id = ?")
        st.setString(1, group.name)
        st.setInt(2, group.id)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
    }

    fun delete(id: Int): String? {
        val st = connection.prepareStatement("DELETE FROM \"group\" WHERE id = ?")
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
        fun ResultSet.getGroup(): Group {
            val id = getInt("id_group")
            val group = getString("group_name")
            return Group(id, group)
        }
    }
}