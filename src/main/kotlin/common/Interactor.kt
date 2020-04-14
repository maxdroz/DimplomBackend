package common

import utils.GetAllParams
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

abstract class Interactor <T> (private val connection: Connection) {
    abstract val tableParams: String
    abstract val tableName: String
    abstract val insertQuery: String
    abstract val editQuery: String

    fun getAll(params: GetAllParams?): List<T> {
        val query = when (params) {
            null -> "SELECT $tableParams FROM $tableName"
            else -> "SELECT $tableParams FROM $tableName ORDER BY ${params.sortBy.toDBName()} ${params.order.name} LIMIT ? OFFSET ?"
        }
        val st = connection.prepareStatement(query)
        when {
            params != null -> {
                st.setInt(1, params.to - params.from)
                st.setInt(2, params.from)
            }
        }
        val res = st.executeQuery()
        val ans = mutableListOf<T>()
        while (res.next()) {
            ans.add(res.getObject())
        }
        return ans
    }

    open fun String.toDBName(): String {
        return this
    }

    fun get(id: Int): T {
        val st = connection.prepareStatement("SELECT $tableParams FROM $tableName WHERE id = ?")
        st.setInt(1, id)
        val res = st.executeQuery()
        res.next()
        return res.getObject()
    }

    abstract fun addParamsToQueryForInsert(st: PreparedStatement, obj: T)

    fun add(obj: T): T {
        val st = connection.prepareStatement("$insertQuery RETURNING $tableParams")
        addParamsToQueryForInsert(st, obj)
        val res = st.executeQuery()
        res.next()
        return res.getObject()
    }

    abstract fun addParamsToQueryForEdit(st: PreparedStatement, obj: T)

    fun edit(obj: T): T {
        val st = connection.prepareStatement("$editQuery RETURNING $tableParams")
        addParamsToQueryForEdit(st, obj)
        val res = st.executeQuery()
        res.next()
        return res.getObject()
    }

    fun delete(id: Int): T {
        val st = connection.prepareStatement("DELETE FROM $tableName WHERE id = ? RETURNING $tableParams")
        st.setInt(1, id)
        val res = st.executeQuery()
        res.next()
        return res.getObject()
    }

    fun getCount(): Int {
        val st = connection.createStatement()
        val res = st.executeQuery("SELECT COUNT(*) FROM $tableName")
        res.next()
        return res.getInt(1)
    }

    abstract fun ResultSet.getObject(): T
}