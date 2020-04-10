package discipline

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

    companion object {
        fun ResultSet.getDiscipline(): Discipline {
            val id = getInt("id_discipline")
            val name = getString("name")
            return Discipline(id, name)
        }
    }
}