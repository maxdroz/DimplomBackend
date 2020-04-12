package utils

import com.google.gson.Gson
import io.javalin.http.Context

object ResponseGenerator {
    fun <T> generate(
        ctx: Context,
        clazz: Class<T>,
        gson: Gson = Gson(),
        dataFormatWrong: (T) -> Boolean = { false },
        callback: (T) -> Boolean
    ) {
        val data = gson.fromJson(ctx.body(), clazz)
        if (data == null || dataFormatWrong(data)) {
            ctx.redirect(Path.WRONG_DATA_FORMAT)
        } else {
            if (callback(data)) {
                ctx.redirect(Path.DB_ERROR)
            } else {
                ctx.redirect(Path.SUCCESS)
            }
        }
    }
}