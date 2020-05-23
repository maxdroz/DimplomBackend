package utils

import com.google.gson.*
import io.javalin.http.Handler
import java.lang.Exception

object AddHeader {
    val addJSONHeader = Handler { ctx ->
        ctx.header("Content-Type", "application/json")
    }
    val addCrossOriginHeader = Handler { ctx ->
//        ctx.header("Access-Control-Allow-Credentials", "true")
        if(ctx.method().toLowerCase() == "options") return@Handler
        ctx.header("Access-Control-Allow-Origin", "*")
        ctx.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
        ctx.header("Access-Control-Allow-Headers", "append,delete,entries,foreach,get,has,keys,set,values,Authorization")
        ctx.header("Access-Control-Expose-Headers", "X-Total-Count,DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range")
    }
}