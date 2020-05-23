package office

import com.google.gson.Gson
import common.getCommonFilterWithSql
import common.getIds
import common.responseCanDelete
import io.javalin.http.Context
import io.javalin.http.Handler
import utils.getAllParams

object OfficeController {
    val fetchAllOffices = Handler { ctx ->
        val filters = ctx.getCommonFilterWithSql()
        ctx.header("X-Total-Count", Main.officeInteractor.getAllCountNoPagination(filters).toString())
        ctx.json(Main.officeInteractor.getAll(ctx.getAllParams(), filters))
    }

    val getOffice = Handler {ctx ->
        ctx.json(Main.officeInteractor.get(ctx.getParamId()))
    }

    val addOffice = Handler { ctx ->
        val data = Gson().fromJson(ctx.body(), Office::class.java)
        ctx.json(Main.officeInteractor.add(data))
    }

    val editOffice = Handler { ctx ->
        val data = Gson().fromJson(ctx.body(), Office::class.java).copy(id = ctx.getParamId())
        ctx.json(Main.officeInteractor.edit(data))
    }

    val deleteOffice = Handler { ctx ->
        ctx.json(Main.officeInteractor.delete(ctx.getParamId()))
    }

    val canDeleteOffice = Handler { ctx ->
        ctx.responseCanDelete(Main.officeInteractor.canBeDeleted(ctx.getIds()))
    }

    private fun Context.getParamId(): Int {
        return pathParam(":office-id").toIntOrNull() ?: OFFICE_INVALID_ID
    }
}