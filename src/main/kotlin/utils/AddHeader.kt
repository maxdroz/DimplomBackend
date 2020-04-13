package utils

import com.google.gson.*
import io.javalin.http.Handler
import java.lang.Exception

object AddHeader {
    val addJSONHeader = Handler { ctx ->
        ctx.header("Content-Type", "application/json")
    }
    val addCrossOriginHeader = Handler { ctx ->
        ctx.header("Access-Control-Allow-Credentials", "true")
        ctx.header("Access-Control-Allow-Origin", "*")
        ctx.header("Access-Control-Expose-Headers", "X-Total-Count")
    }
    val addXTotalCountHeader = Handler { ctx ->
        val res = ctx.resultString() ?: return@Handler
        val json = try {
            JsonParser.parseString(res)
        } catch (e: Exception) {
            return@Handler
        }
        val size = if (json.isJsonArray) {
            json.asJsonArray.size()
        } else {
            1
        }
        ctx.header("X-Total-Count", size.toString())
    }
}