package discipline

import io.javalin.http.Context
import io.javalin.http.Handler
import response.showError
import teacher.TEACHER_INVALID_ID
import utils.Path
import utils.ResponseGenerator

object DisciplineController {
    val fetchAllDisciplines = Handler { ctx ->
        ctx.json(Main.disciplineInteractor.getAll())
    }

    val getDiscipline = Handler { ctx ->
        ctx.json(Main.disciplineInteractor.get(ctx.getParamId()))
    }

    val addDiscipline = Handler { ctx ->
        ResponseGenerator.generate(ctx, Discipline::class.java) {
            Main.disciplineInteractor.add(it)
        }
    }

    val editDiscipline = Handler { ctx ->
        ResponseGenerator.generate(ctx, Discipline::class.java) {
            Main.disciplineInteractor.edit(it.copy(id = ctx.getParamId()))
        }
    }

    val deleteDiscipline = Handler { ctx ->
        val errorMessage = Main.disciplineInteractor.delete(ctx.getParamId())
        if (errorMessage != null) {
            ctx.showError(errorMessage)
        } else {
            ctx.redirect(Path.SUCCESS)
        }
    }

    private fun Context.getParamId(): Int {
        return pathParam(":discipline-id").toIntOrNull() ?: DISCIPLINE_INVALID_ID
    }
}