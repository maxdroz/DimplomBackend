package discipline

import common.Interactor
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class DisciplineInteractorNew(connection: Connection) : Interactor<Discipline>(connection) {
    override val tableParams: String
        get() = "id as id_discipline, name"
    override val tableName: String
        get() = "discipline"
    override val insertQuery: String
        get() = "INSERT INTO discipline VALUES (DEFAULT, ?)"
    override val editQuery: String
        get() = "UPDATE discipline SET name = ? WHERE id = ?"

    override fun addParamsToQueryForInsert(st: PreparedStatement, obj: Discipline) {
        st.setString(1, obj.name)
    }

    override fun addParamsToQueryForEdit(st: PreparedStatement, obj: Discipline) {
        st.setString(1, obj.name)
        st.setInt(2, obj.id)
    }

    override fun ResultSet.getObject(): Discipline {
        val id = getInt("id_discipline")
        val name = getString("name")
        return Discipline(id, name)
    }

}