package teacher

import io.javalin.http.Context
import io.javalin.http.Handler
import response.showError
import utils.Path
import utils.ResponseGenerator

object TeacherController {
    val fetchAllTeachers = Handler { ctx ->
        ctx.json(Main.teacherInteractor.getAll())
    }

    val getTeacher = Handler { ctx ->
        ctx.json(Main.teacherInteractor.get(ctx.getParamId()))
    }

    val addTeacher = Handler { ctx ->
        ResponseGenerator.generate(ctx, Teacher::class.java) {
            Main.teacherInteractor.add(it)
        }
    }

    val editTeacher = Handler { ctx ->
        ResponseGenerator.generate(ctx, Teacher::class.java) {
                Main.teacherInteractor.edit(it.copy(id = ctx.getParamId()))
            }
    }

    val deleteTeacher = Handler { ctx ->
        val errorMessage = Main.teacherInteractor.delete(ctx.getParamId())
        if (errorMessage != null) {
            ctx.showError(errorMessage)
        } else {
            ctx.redirect(Path.SUCCESS)
        }
    }

    private fun Context.getParamId(): Int {
        return pathParam(":teacher-id").toIntOrNull() ?: TEACHER_INVALID_ID
    }
}
