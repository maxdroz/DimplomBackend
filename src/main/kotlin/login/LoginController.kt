package login

import io.javalin.http.Handler
import utils.Path

class LoginController {
    companion object {
        val ensureLoginBeforeEditing = Handler { ctx ->
            if(ctx.method() == "GET") {
                return@Handler
            }
            if(ctx.cookie("logged") == null) {
                ctx.redirect(Path.LOGIN_REQUIRED)
            }
        }
    }
}