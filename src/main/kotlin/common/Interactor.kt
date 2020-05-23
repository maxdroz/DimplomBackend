package common

import utils.GetAllParams
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

abstract class Interactor<T, FilterClass : Filter>(private val connection: Connection) {
    abstract val tableParams: String
    abstract val tableName: String
    abstract val insertQuery: String
    abstract val editQuery: String
    abstract val referenceName: String

    abstract fun addParamsToQueryForSearch(st: PreparedStatement, filter: FilterClass)
    abstract fun getSearchPathQuery(filter: FilterClass): String

    fun getAll(params: GetAllParams?, filter: FilterClass?): List<T> {
        val searchQuery = if (filter == null) "" else getSearchPathQuery(filter)
        val query = when (params) {
            null -> "SELECT $tableParams FROM $tableName $searchQuery"
            else -> "SELECT $tableParams FROM $tableName $searchQuery ORDER BY ${params.sortBy.toDBName()} ${params.order.name} LIMIT ? OFFSET ?"
        }
        val st = connection.prepareStatement(query)
        if (filter != null) {
            addParamsToQueryForSearch(st, filter)
        }
        val paramInSearchCount = if (filter != null) {
            getSearchPathQuery(filter).count { it == '?' }
        } else {
            0
        }
        when {
            params != null -> {
                st.setInt(paramInSearchCount + 1, params.to - params.from)
                st.setInt(paramInSearchCount + 2, params.from)
            }
        }
        val res = st.executeQuery()
        val ans = mutableListOf<T>()
        while (res.next()) {
            ans.add(res.getObject())
        }
        return ans
    }

    fun getAllCountNoPagination(filter: FilterClass?): Int {
        if(filter == null) return getCount()
        val query = "SELECT COUNT(*) FROM $tableName ${getSearchPathQuery(filter)}"
        val st = connection.prepareStatement(query)
        addParamsToQueryForSearch(st, filter)

        val res = st.executeQuery()
        return if (res.next()) {
            res.getInt(1)
        } else {
            0
        }
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

    private fun getCount(): Int {
        val st = connection.createStatement()
        val res = st.executeQuery("SELECT COUNT(*) FROM $tableName")
        res.next()
        return res.getInt(1)
    }

    fun canBeDeleted(ids: List<Int>): Boolean {
        val st = connection.prepareStatement("SELECT COUNT(*) FROM lesson WHERE $referenceName = ANY (?)")
        val arr = connection.createArrayOf("integer", ids.toTypedArray())
        st.setArray(1, arr)
        val res = st.executeQuery()
        res.next()
        return res.getInt(1) == 0
    }

    abstract fun ResultSet.getObject(): T
}