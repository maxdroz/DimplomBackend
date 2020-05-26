package login

import io.javalin.core.security.AccessManager
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.Handler
import utils.Path

class LoginController {
    companion object {
        val accessManager = AccessManager { handler, ctx, permittedRoles ->
            when {
                permittedRoles.contains(Roles.ANYONE) -> handler.handle(ctx)
                ctx.getRolesOfCurrentUser().any { permittedRoles.contains(it) } -> handler.handle(ctx)
                else -> ctx.status(401).json("not suthorized LOL")
            }
        }
    }
}

private fun Context.getRolesOfCurrentUser(): Set<Roles> {
    if(!basicAuthCredentialsExist()) return emptySet()
    return Main.userInteractor.getUserRoles(basicAuthCredentials())
}