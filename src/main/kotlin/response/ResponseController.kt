package response

import com.google.gson.Gson
import io.javalin.http.Handler

object ResponseController {
    val loginRequiredError = Handler { ctx ->
        ctx.html(Gson().toJson(mapOf("result" to "error_login_required")))
    }

    val success = Handler { ctx ->
        ctx.html(Gson().toJson(mapOf("result" to "success")))
    }

    val wrongDataFormat = Handler { ctx ->
        ctx.html(Gson().toJson(mapOf("result" to "wrong_data_format")))
    }

    val db_error = Handler { ctx ->
        ctx.html(Gson().toJson(mapOf("result" to "db_error")))
    }
}