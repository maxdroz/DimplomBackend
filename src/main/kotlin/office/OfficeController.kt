package office

import io.javalin.http.Context
import io.javalin.http.Handler
import response.showError
import utils.Path
import utils.ResponseGenerator

object OfficeController {
    val fetchAllOffices = Handler { ctx ->
        ctx.json(Main.officeInteractor.getAll())
    }

    val getOffice = Handler {ctx ->
        ctx.json(Main.officeInteractor.get(ctx.getParamId()))
    }

    val addOffice = Handler { ctx ->
        ResponseGenerator.generate(ctx, Office::class.java) {
            Main.officeInteractor.add(it)
        }
    }

    val editOffice = Handler { ctx ->
        ResponseGenerator.generate(ctx, Office::class.java) {
            Main.officeInteractor.edit(it.copy(id = ctx.getParamId()))
        }
    }

    val deleteOffice = Handler { ctx ->
        val errorMessage = Main.officeInteractor.delete(ctx.getParamId())
        if (errorMessage != null) {
            ctx.showError(errorMessage)
        } else {
            ctx.redirect(Path.SUCCESS)
        }
    }

    private fun Context.getParamId(): Int {
        return pathParam(":office-id").toIntOrNull() ?: OFFICE_INVALID_ID
    }
}