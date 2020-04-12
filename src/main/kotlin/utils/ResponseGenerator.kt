package utils

import com.google.gson.Gson
import io.javalin.http.Context
import response.showError

object ResponseGenerator {
    fun <T> generate(
        ctx: Context,
        clazz: Class<T>,
        gson: Gson = Gson(),
        dataFormatWrong: (T) -> Boolean = { false },
        callback: (T) -> String?
    ) {
        val data = gson.fromJson(ctx.body(), clazz)
        if (data == null || dataFormatWrong(data)) {
            ctx.showError("wrong_data_format")
        } else {
            val errorMessage = callback(data)
            if (errorMessage != null) {
                ctx.showError(errorMessage)
            } else {
                ctx.redirect(Path.SUCCESS)
            }
        }
    }
}