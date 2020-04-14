package utils

import io.javalin.http.Context

data class GetAllParams(val order: Sort, val sortBy: String, val from: Int, val to: Int)

fun Context.getAllParams(): GetAllParams? {
    val start = queryParam("_start")?.toInt() ?: return null
    val end = queryParam("_end")?.toInt() ?: return null
    val order = queryParam("_order")?.toOrder() ?: return null
    val sortBy = queryParam("_sort") ?: return null
    return GetAllParams(order, sortBy, start, end)
}

fun String.toOrder(): Sort {
    if(toLowerCase() == "asc") {
        return Sort.ASC
    }
    return Sort.DESC
}