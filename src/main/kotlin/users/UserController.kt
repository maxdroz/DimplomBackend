package users

import com.google.common.hash.Hashing
import com.google.gson.Gson
import common.getCommonFilterWithSql
import io.javalin.http.Context
import io.javalin.http.Handler
import utils.getAllParams
import java.nio.charset.StandardCharsets
import java.util.*

object UserController {
    val fetchAllUsers = Handler { ctx ->
        val filters = ctx.getCommonFilterWithSql()
        ctx.header("X-Total-Count", Main.userInteractor.getAllCountNoPagination(filters).toString())
        ctx.json(Main.userInteractor.getAll(ctx.getAllParams(), filters).map { it.toUserViewWithHiddenPassword() })
    }

    val getUser = Handler { ctx ->
        ctx.json(Main.userInteractor.getUserByUsername(ctx.getUsername())!!.toUserViewWithHiddenPassword().copy(hashedPassword = ""))
    }

    val addUser = Handler { ctx ->
        val data = Gson().fromJson(ctx.body(), UserView::class.java)
        ctx.json(Main.userInteractor.add(data.toUserViewRawPassword().toUserModel()))
    }

    val editUser = Handler { ctx ->
        val data = Gson().fromJson(ctx.body(), UserView::class.java).toUserViewRawPassword()
        val userData = data.toUserModel()
        val resp = if(data.password.isBlank()) {
            Main.userInteractor.updateRole(userData.username, userData.roles)
        } else {
            Main.userInteractor.updatePasswordAndRole(userData.username, userData.hashedPassword, userData.roles)
        }
        ctx.json(resp.toUserViewWithHiddenPassword())
    }

    val deleteUser = Handler { ctx ->
        ctx.json(Main.userInteractor.deleteUser(ctx.getUsername()))
    }

    val updatePassword = Handler { ctx ->
        val user = Main.userInteractor.getUserByUsername(ctx.basicAuthCredentials().username)
        val changePassword = Gson().fromJson(ctx.body(), ChangePasswordView::class.java)
        if(changePassword.oldPassword.hashSHA256() == user?.hashedPassword) {
            Main.userInteractor.updatePassword(user.username, changePassword.password.hashSHA256())
            ctx.json(mapOf("success" to true, "token" to generateToken(user.username, changePassword.password)))
        } else {
            ctx.json(mapOf("success" to false))
        }
    }

    val authorizeCallback = Handler { ctx ->
        val user = Gson().fromJson(ctx.body(), UserSimple::class.java)
        val dbUser = Main.userInteractor.getUserByUsername(user.username)
        if (dbUser == null || user.password.hashSHA256() != dbUser.hashedPassword) {
            ctx.status(401).json(mapOf("message" to "Text"))
        } else {
            ctx.json(
                mapOf(
                    "credentials" to "Basic ${generateToken(dbUser.username, dbUser.hashedPassword)}",
                    "roles" to dbUser.roles,
                    "username" to dbUser.username
                )
            )
        }
    }

    private fun generateToken(username: String, hashedPassword: String): String {
        return Base64.getEncoder().encode(("$username:$hashedPassword").toByteArray())
            .toString(StandardCharsets.UTF_8)
    }

    val isUsernameTaken = Handler { ctx ->
        val username = ctx.body()
        ctx.html((Main.userInteractor.getUserByUsername(username) != null).toString())
    }

    private fun Context.getUsername(): String {
        return pathParam(":username")
    }
}

fun String.hashSHA256(): String {
    return Hashing.sha256().hashString(this, StandardCharsets.UTF_8).toString()
}