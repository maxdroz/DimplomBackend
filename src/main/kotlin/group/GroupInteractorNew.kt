package group

import common.Interactor
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class GroupInteractorNew(connection: Connection) : Interactor<Group>(connection) {
    override val tableParams: String
        get() = "id as id_group, name as group_name"
    override val tableName: String
        get() = "\"group\""
    override val insertQuery: String
        get() = "INSERT INTO \"group\" VALUES (DEFAULT, ?)"
    override val editQuery: String
        get() = "UPDATE \"group\" SET name = ? WHERE id = ?"

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

}