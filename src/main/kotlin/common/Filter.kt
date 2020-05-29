package common

import io.javalin.http.Context

interface Filter

class CommonFilter(
    val q: String
): Filter

data class LessonFilter(
    val q: String?,
    val teacher: Int?,
    val office: Int?,
    val group: Int?,
    val discipline: Int?
): Filter

fun Context.getLessonFilter(): LessonFilter? {
    val query = queryParam("q")
    val q = query.takeIf { it?.isNotBlank() == true }
    val qu = if(q != null) {
        "%$q%"
    } else null
    val teacher = queryParam("teacher")?.toIntOrNull()
    val office = queryParam("office")?.toIntOrNull()
    val group = queryParam("group")?.toIntOrNull()
    val discipline = queryParam("discipline")?.toIntOrNull()
    if(query == null && teacher == null && office == null && group == null && discipline == null) {
        return null
    }
    return LessonFilter(qu, teacher, office, group, discipline)
}

fun Context.getCommonFilterWithSql(): CommonFilter? {
    val query = queryParam("q") ?: return null
    return CommonFilter("%$query%")
}