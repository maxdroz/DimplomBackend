package discipline

import common.CommonFilter
import common.Filter
import common.Interactor
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class DisciplineInteractorNew(connection: Connection) : Interactor<Discipline, CommonFilter>(connection) {

    override val tableParams: String
        get() = "id as id_discipline, name"
    override val tableName: String
        get() = "discipline"
    override val insertQuery: String
        get() = "INSERT INTO discipline VALUES (DEFAULT, ?)"
    override val editQuery: String
        get() = "UPDATE discipline SET name = ? WHERE id = ?"
    override val referenceName: String
        get() = "id_discipline"

    override fun getSearchPathQuery(filter: CommonFilter): String = "WHERE CONCAT(id, ' ', name) LIKE ?"

    override fun addParamsToQueryForSearch(st: PreparedStatement, filter: CommonFilter) {
        st.setString(1, filter.q)
    }

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