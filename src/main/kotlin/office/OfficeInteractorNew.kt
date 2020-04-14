package office

import common.Interactor
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class OfficeInteractorNew(connection: Connection): Interactor<Office>(connection) {
    override val tableParams: String
        get() = "id as id_office, office"
    override val tableName: String
        get() = "office"
    override val insertQuery: String
        get() = "INSERT INTO office VALUES (DEFAULT, ?)"
    override val editQuery: String
        get() = "UPDATE office SET office = ? WHERE id = ?"

    override fun addParamsToQueryForInsert(st: PreparedStatement, obj: Office) {
        st.setString(1, obj.office)
    }

    override fun addParamsToQueryForEdit(st: PreparedStatement, obj: Office) {
        st.setString(1, obj.office)
        st.setInt(2, obj.id)
    }

    override fun ResultSet.getObject(): Office {
        val id = getInt("id_office")
        val office = getString("office")
        return Office(id, office)
    }

}