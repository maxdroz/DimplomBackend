package discipline

import com.google.gson.Gson
import io.javalin.http.Handler
import utils.ResponseGenerator

object DisciplineController {
    val fetchAllDisciplines = Handler { ctx ->
        ctx.html(Gson().toJson(hashMapOf("disciplines" to Main.disciplineInteractor.getAll())))
    }

    val addDiscipline = Handler { ctx ->
        ResponseGenerator.generate(ctx, Discipline::class.java) {
            Main.disciplineInteractor.add(it)
        }
    }

    val editDiscipline = Handler { ctx ->
        ResponseGenerator.generate(ctx, Discipline::class.java,
            dataFormatWrong = {
                it.id == DISCIPLINE_INVALID_ID
            },
            callback = {
                Main.disciplineInteractor.edit(it)
            }
        )
    }

    val deleteDiscipline = Handler { ctx ->
        ResponseGenerator.generate(ctx, Discipline::class.java,
            dataFormatWrong = {
                it.id == DISCIPLINE_INVALID_ID
            },
            callback = {
                Main.disciplineInteractor.delete(it.id)
            }
        )
    }
}