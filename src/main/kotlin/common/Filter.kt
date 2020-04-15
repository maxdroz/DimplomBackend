package common

import io.javalin.http.Context

interface Filter

class CommonFilter(
    val q: String
): Filter

data class LessonFilter(
    val teacher: Int?,
    val office: Int?,
    val group: Int?
): Filter

fun Context.getLessonFilter(): LessonFilter? {
    val teacher = queryParam("teacher")?.toIntOrNull()
    val office = queryParam("office")?.toIntOrNull()
    val group = queryParam("group")?.toIntOrNull()
    if(teacher == null && office == null && group == null) {
        return null
    }
    return LessonFilter(teacher, office, group)
}

fun Context.getCommonFilterWithSql(): CommonFilter? {
    val query = queryParam("q") ?: return null
    return CommonFilter("%$query%")
}