package teacher

import common.CommonFilter
import common.Interactor
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class TeacherInteractorNew(connection: Connection): Interactor<Teacher, CommonFilter>(connection) {
    override val tableParams: String
        get() = "id as id_teacher, name as teacher_name, surname, patronymic, phone_number, description"
    override val tableName: String
        get() = "teacher"
    override val insertQuery: String
        get() = "INSERT INTO teacher values (DEFAULT, ?, ?, ?, ?, ?)"
    override val editQuery: String
        get() = "UPDATE teacher SET name = ?, surname = ?, patronymic = ?, phone_number = ?, description = ? WHERE id = ?"
    override val referenceName: String
        get() = "id_teacher"

    override fun getSearchPathQuery(filter: CommonFilter): String = "WHERE LOWER(CONCAT(id, ' ', name, ' ', surname, ' ', patronymic, ' ', phone_number, ' ', description)) LIKE LOWER(?)"

    override fun addParamsToQueryForSearch(st: PreparedStatement, filter: CommonFilter) {
        st.setString(1, filter.q)
    }

    override fun addParamsToQueryForInsert(st: PreparedStatement, obj: Teacher) {
        st.setString(1, obj.name)
        st.setString(2, obj.surname)
        st.setString(3, obj.patronymic)
        st.setString(4, obj.phoneNumber)
        st.setString(5, obj.description)
    }

    override fun addParamsToQueryForEdit(st: PreparedStatement, obj: Teacher) {
        st.setString(1, obj.name)
        st.setString(2, obj.surname)
        st.setString(3, obj.patronymic)
        st.setString(4, obj.phoneNumber)
        st.setString(5, obj.description)
        st.setInt(6, obj.id)
    }

    override fun ResultSet.getObject(): Teacher {
        val id = getInt("id_teacher")
        val name = getString("teacher_name")
        val surname = getString("surname")
        val patronymic = getString("patronymic")
        val phoneNumber = getString("phone_number")
        val description = getString("description")
        return Teacher(id, name, surname, patronymic, phoneNumber, description)
    }

}