package teacher

import com.google.gson.Gson
import io.javalin.http.Handler
import utils.ResponseGenerator

object TeacherController {
    val fetchAllTeachers = Handler { ctx ->
        ctx.html(Gson().toJson(hashMapOf("teachers" to Main.teacherInteractor.getAll())))
    }

    val addTeacher = Handler { ctx ->
        ResponseGenerator.generate(ctx, Teacher::class.java) {
            Main.teacherInteractor.add(it)
        }
    }

    val editTeacher = Handler { ctx ->
        ResponseGenerator.generate(ctx, Teacher::class.java,
            dataFormatWrong = {
                it.id == TEACHER_INVALID_ID
            },
            callback = {
                Main.teacherInteractor.edit(it)
            }
        )
    }

    val deleteTeacher = Handler { ctx ->
        ResponseGenerator.generate(ctx, Teacher::class.java,
            dataFormatWrong = {
                it.id == TEACHER_INVALID_ID
            },
            callback = {
                Main.teacherInteractor.delete(it.id)
            }
        )
    }
}
