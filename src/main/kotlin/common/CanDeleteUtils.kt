package common

import com.google.gson.Gson
import io.javalin.http.Context

fun Context.getIds(): List<Int> {
    return Gson().fromJson(body(), IntArray::class.java).toList()
}

fun Context.responseCanDelete(canDelete: Boolean) {
    if(canDelete) {
        json(mapOf("result" to "success"))
    } else {
        json(mapOf("result" to "failure"))
    }
}