package office

import com.google.gson.Gson
import io.javalin.http.Handler
import utils.ResponseGenerator

object OfficeController {
    val fetchAllOffices = Handler { ctx ->
        ctx.html(Gson().toJson(hashMapOf("offices" to Main.officeInteractor.getAll())))
    }

    val addOffice = Handler { ctx ->
        ResponseGenerator.generate(ctx, Office::class.java) {
            Main.officeInteractor.add(it)
        }
    }

    val editOffice = Handler { ctx ->
        ResponseGenerator.generate(ctx, Office::class.java,
            dataFormatWrong = {
                it.id == OFFICE_INVALID_ID
            },
            callback = {
                Main.officeInteractor.edit(it)
            }
        )
    }

    val deleteOffice = Handler { ctx ->
        ResponseGenerator.generate(ctx, Office::class.java,
            dataFormatWrong = {
                it.id == OFFICE_INVALID_ID
            },
            callback = {
                Main.officeInteractor.delete(it.id)
            }
        )
    }
}