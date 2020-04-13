package group

import io.javalin.http.Context
import io.javalin.http.Handler
import response.showError
import utils.Path
import utils.ResponseGenerator

object GroupController {
    val fetchAllGroups = Handler { ctx ->
        ctx.json(Main.groupInteractor.getAll())
    }

    val getGroup = Handler { ctx ->
        ctx.json(Main.groupInteractor.get(ctx.getParamId()))
    }

    val addGroup = Handler { ctx ->
        ResponseGenerator.generate(ctx, Group::class.java) {
            Main.groupInteractor.add(it)
        }
    }

    val editGroup = Handler { ctx ->
        ResponseGenerator.generate(ctx, Group::class.java) {
            Main.groupInteractor.edit(it.copy(id = ctx.getParamId()))
        }
    }

    val deleteGroup = Handler { ctx ->
        val errorMessage = Main.groupInteractor.delete(ctx.getParamId())
        if (errorMessage != null) {
            ctx.showError(errorMessage)
        } else {
            ctx.redirect(Path.SUCCESS)
        }
    }

    private fun Context.getParamId(): Int {
        return pathParam(":group-id").toIntOrNull() ?: GROUP_INVALID_ID
    }
}