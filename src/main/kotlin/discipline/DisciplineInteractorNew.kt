package discipline

import common.CommonFilter
import common.Filter
import common.Interactor
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class DisciplineInteractorNew(connection: Connection) : Interactor<Discipline, CommonFilter>(connection) {

    override val tableParams: String
        get() = "id as id_discipline, name, short_name"
    override val tableName: String
        get() = "discipline"
    override val insertQuery: String
        get() = "INSERT INTO discipline VALUES (DEFAULT, ?, ?)"
    override val editQuery: String
        get() = "UPDATE discipline SET name = ?, short_name = ? WHERE id = ?"
    override val referenceName: String
        get() = "id_discipline"

    override fun getSearchPathQuery(filter: CommonFilter): String = "WHERE LOWER(CONCAT(id, ' ', name, ' ', short_name)) LIKE LOWER(?)"

    override fun addParamsToQueryForSearch(st: PreparedStatement, filter: CommonFilter) {
        st.setString(1, filter.q)
    }

    override fun addParamsToQueryForInsert(st: PreparedStatement, obj: Discipline) {
        st.setString(1, obj.name)
        st.setString(2, obj.shortName)
    }

    override fun addParamsToQueryForEdit(st: PreparedStatement, obj: Discipline) {
        st.setString(1, obj.name)
        st.setString(2, obj.shortName)
        st.setInt(3, obj.id)
    }

    override fun ResultSet.getObject(): Discipline {
        val id = getInt("id_discipline")
        val name = getString("name")
        val shortName = getString("short_name")
        return Discipline(id, name, shortName)
    }

    override fun String.toDBName(): String {
        return when (this.toLowerCase()) {
            "shortname" -> "short_name"
            else -> this
        }
    }
}