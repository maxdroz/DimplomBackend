package response

import com.google.gson.Gson
import io.javalin.http.Context
import io.javalin.http.Handler
import response.ResponseController.ERROR_REASON_KEY
import utils.Path

object ResponseController {
    val loginRequiredError = Handler { ctx ->
        ctx.html(Gson().toJson(mapOf("result" to "error_login_required")))
    }

    val success = Handler { ctx ->
        ctx.html(Gson().toJson(mapOf("result" to "success")))
    }

    val error = Handler { ctx ->
        val message = ctx.sessionAttribute<String>(ERROR_REASON_KEY) ?: "unknown_error"
        ctx.sessionAttribute(ERROR_REASON_KEY, null)
        ctx.html(Gson().toJson(mapOf("result" to message)))
    }

    const val ERROR_REASON_KEY = "error_reason"
}

fun Context.showError(reason: String) {
    sessionAttribute(ERROR_REASON_KEY, reason)
//    redirect(Path.ERROR)
}
