package discipline

import com.google.gson.Gson
import common.getCommonFilterWithSql
import common.getIds
import common.responseCanDelete
import io.javalin.http.Context
import io.javalin.http.Handler
import utils.getAllParams

object DisciplineController {
    val fetchAllDisciplines = Handler { ctx ->
        ctx.json(Main.disciplineInteractor.getAll(ctx.getAllParams(), ctx.getCommonFilterWithSql()))
    }

    val getDiscipline = Handler { ctx ->
        ctx.json(Main.disciplineInteractor.get(ctx.getParamId()))
    }

    val addDiscipline = Handler { ctx ->
        val data = Gson().fromJson(ctx.body(), Discipline::class.java)
        ctx.json(Main.disciplineInteractor.add(data))
    }

    val editDiscipline = Handler { ctx ->
        val data = Gson().fromJson(ctx.body(), Discipline::class.java).copy(id = ctx.getParamId())
        ctx.json(Main.disciplineInteractor.edit(data))
    }

    val deleteDiscipline = Handler { ctx ->
        ctx.json(Main.disciplineInteractor.delete(ctx.getParamId()))
    }

    val canDeleteDiscipline = Handler { ctx ->
        ctx.responseCanDelete(Main.disciplineInteractor.canBeDeleted(ctx.getIds()))
    }

    private fun Context.getParamId(): Int {
        return pathParam(":discipline-id").toIntOrNull() ?: DISCIPLINE_INVALID_ID
    }
}