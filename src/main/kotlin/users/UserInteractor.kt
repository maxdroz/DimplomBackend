package users

import common.CommonFilter
import common.Interactor
import io.javalin.core.security.BasicAuthCredentials
import login.Roles
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class UserInteractor(private val connection: Connection) : Interactor<UserModel, CommonFilter>(connection) {

    override val tableParams: String
        get() = "username, hashed_password, roles"
    override val tableName: String
        get() = "admin_panel_user"
    override val insertQuery: String
        get() = "INSERT INTO admin_panel_user VALUES (?, ?, ?)"
    override val editQuery: String
        get() = "UPDATE admin_panel_user SET hashed_password = ?, roles = ? WHERE username = ?"
    override val referenceName: String
        get() = ""

    override fun getSearchPathQuery(filter: CommonFilter): String = "WHERE LOWER(username) LIKE LOWER(?)"

    override fun addParamsToQueryForSearch(st: PreparedStatement, filter: CommonFilter) {
        st.setString(1, filter.q)
    }

    override fun addParamsToQueryForInsert(st: PreparedStatement, obj: UserModel) {
        st.setString(1, obj.username)
        st.setString(2, obj.hashedPassword)
        st.setArray(3, connection.createArrayOf("text", obj.roles.map { it.name }.toTypedArray()))
    }

    override fun addParamsToQueryForEdit(st: PreparedStatement, obj: UserModel) {
        st.setString(1, obj.hashedPassword)
        st.setArray(2, connection.createArrayOf("text", obj.roles.map { it.name }.toTypedArray()))
        st.setString(3, obj.username)
    }

    override fun ResultSet.getObject(): UserModel {
        return UserModel(
            username = getString(1),
            hashedPassword = getString(2),
            roles = (getArray(3).array as Array<String>).toRoleSet()
        )
    }

    private fun Array<String>.toRoleSet(): Set<Roles> {
        return this.map { Roles.valueOf(it) }.toHashSet()
    }

    fun getUserByUsername(username: String): UserModel? {
        val st = connection.prepareStatement("SELECT $tableParams FROM $tableName WHERE username = ?")
        st.setString(1, username)
        val res = st.executeQuery()
        return if (res.next()) {
            res.getObject()
        } else {
            null
        }
    }

    fun getUserRoles(credentials: BasicAuthCredentials): Set<Roles> {
        val st = connection.prepareStatement("SELECT roles FROM $tableName WHERE username = ? AND hashed_password = ?")
        st.setString(1, credentials.username)
        st.setString(2, credentials.password)
        val res = st.executeQuery()
        return if (res.next()) {
            (res.getArray(1).array as Array<String>).toRoleSet()
        } else {
            emptySet()
        }
    }

    fun updateRole(username: String, roles: Set<Roles>): UserModel {
        val st = connection.prepareStatement("UPDATE $tableName SET roles = ? WHERE username = ? RETURNING $tableParams")
        st.setArray(1, connection.createArrayOf("text", roles.map { it.name }.toTypedArray()))
        st.setString(2, username)
        val res = st.executeQuery()
        res.next()
        return res.getObject()
    }

    fun updatePasswordAndRole(username: String, hashedPassword: String, roles: Set<Roles>): UserModel {
        val st = connection.prepareStatement("UPDATE $tableName SET roles = ?, hashed_password = ? WHERE username = ? RETURNING $tableParams")
        st.setArray(1, connection.createArrayOf("text", roles.map { it.name }.toTypedArray()))
        st.setString(2, hashedPassword)
        st.setString(3, username)
        val res = st.executeQuery()
        res.next()
        return res.getObject()
    }

    fun updatePassword(username: String, hashedPassword: String): UserModel {
        val st = connection.prepareStatement("UPDATE $tableName SET hashed_password = ? WHERE username = ? RETURNING $tableParams")
        st.setString(1, hashedPassword)
        st.setString(2, username)
        val res = st.executeQuery()
        res.next()
        return res.getObject()
    }

    fun deleteUser(username: String): UserModel {
        val st = connection.prepareStatement("DELETE FROM $tableName WHERE username = ? RETURNING $tableParams")
        st.setString(1, username)
        val res = st.executeQuery()
        res.next()
        return res.getObject()
    }

    override fun String.toDBName(): String {
        return when(this) {
            "id" -> "username"
            else -> this
        }
    }
}