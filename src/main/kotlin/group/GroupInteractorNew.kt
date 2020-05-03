package group

import common.CommonFilter
import common.Filter
import common.Interactor
import utils.GetAllParams
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class GroupInteractorNew(connection: Connection) : Interactor<Group, CommonFilter>(connection) {
    override val tableParams: String
        get() = "id as id_group, name as group_name"
    override val tableName: String
        get() = "\"group\""
    override val insertQuery: String
        get() = "INSERT INTO \"group\" VALUES (DEFAULT, ?)"
    override val editQuery: String
        get() = "UPDATE \"group\" SET name = ? WHERE id = ?"
    override val referenceName: String
        get() = "id_group"

    override fun getSearchPathQuery(filter: CommonFilter): String = "WHERE CONCAT(id, ' ', name) LIKE ?"

    override fun addParamsToQueryForSearch(st: PreparedStatement, filter: CommonFilter) {
        st.setString(1, filter.q)
    }

    override fun addParamsToQueryForInsert(st: PreparedStatement, obj: Group) {
        st.setString(1, obj.name)
    }

    override fun addParamsToQueryForEdit(st: PreparedStatement, obj: Group) {
        st.setString(1, obj.name)
        st.setInt(2, obj.id)
    }

    override fun ResultSet.getObject(): Group {
        val id = getInt("id_group")
        val group = getString("group_name")
        return Group(id, group)
    }

    fun getAllUser(params: GetAllParams?, filter: CommonFilter?): List<String> {
        return getAll(params, filter).map { it.name }
    }
}