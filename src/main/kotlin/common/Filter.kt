package common

import io.javalin.http.Context

interface Filter

class CommonFilter(
    val q: String
): Filter

data class LessonFilter(
    val teacher: Int?,
    val office: Int?,
    val group: Int?,
    val discipline: Int?
): Filter

fun Context.getLessonFilter(): LessonFilter? {
    val teacher = queryParam("teacher")?.toIntOrNull()
    val office = queryParam("office")?.toIntOrNull()
    val group = queryParam("group")?.toIntOrNull()
    val discipline = queryParam("discipline")?.toIntOrNull()
    if(teacher == null && office == null && group == null && discipline == null) {
        return null
    }
    return LessonFilter(teacher, office, group, discipline)
}

fun Context.getCommonFilterWithSql(): CommonFilter? {
    val query = queryParam("q") ?: return null
    return CommonFilter("%$query%")
}