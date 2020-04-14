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
    val addXTotalCountHeader = Handler { ctx ->
        val index = ctx.path().indexOf('/', 1)
        @Suppress("MoveVariableDeclarationIntoWhen")
        val path = if(index == -1) ctx.path() else ctx.path().substring(0, index)
        val size = when (path) {
            Path.GROUPS -> {
                Main.groupInteractor.getCount()
            }
            Path.LESSONS -> {
                Main.lessonInteractor.getCount()
            }
            Path.TEACHERS -> {
                Main.teacherInteractor.getCount()
            }
            Path.OFFICES -> {
                Main.officeInteractor.getCount()
            }
            Path.DISCIPLINES -> {
                Main.disciplineInteractor.getCount()
            }
            else -> 0
        }
        ctx.header("X-Total-Count", size.toString())
    }
}