package utils

import io.javalin.http.Handler

class AddJSONHeader {
    companion object {
        val add = Handler { ctx ->
            ctx.header("Content-Type", "application/json")
        }
    }
}