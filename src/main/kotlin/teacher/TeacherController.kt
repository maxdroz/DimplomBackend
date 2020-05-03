package teacher

import com.google.gson.Gson
import common.getCommonFilterWithSql
import common.getIds
import common.responseCanDelete
import io.javalin.http.Context
import io.javalin.http.Handler
import utils.getAllParams

object TeacherController {
    val fetchAllTeachers = Handler { ctx ->
        ctx.json(Main.teacherInteractor.getAll(ctx.getAllParams(), ctx.getCommonFilterWithSql()))
    }

    val getTeacher = Handler { ctx ->
        ctx.json(Main.teacherInteractor.get(ctx.getParamId()))
    }

    val addTeacher = Handler { ctx ->
        val data = Gson().fromJson(ctx.body(), Teacher::class.java)
        ctx.json(Main.teacherInteractor.add(data))
    }

    val editTeacher = Handler { ctx ->
        val data = Gson().fromJson(ctx.body(), Teacher::class.java).copy(id = ctx.getParamId())
        ctx.json(Main.teacherInteractor.edit(data))
    }

    val deleteTeacher = Handler { ctx ->
        ctx.json(Main.teacherInteractor.delete(ctx.getParamId()))
    }

    val canDeleteTeacher = Handler { ctx ->
        ctx.responseCanDelete(Main.teacherInteractor.canBeDeleted(ctx.getIds()))
    }


    private fun Context.getParamId(): Int {
        return pathParam(":teacher-id").toIntOrNull() ?: TEACHER_INVALID_ID
    }
}
