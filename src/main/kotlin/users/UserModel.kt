package users

import common.Model
import login.Roles

data class UserModel(
    val username: String,
    val hashedPassword: String,
    val roles: Set<Roles>
): Model<UserModel> {
    override fun trimmed(): UserModel {
        return copy(username = username.trim())
    }

    fun toUserView(): UserView {
        return UserView(
            username,
            hashedPassword,
            roles.contains(Roles.ADMIN)
        )
    }

    fun toUserViewWithHiddenPassword(): UserView {
        return UserView(
            username,
            "**********",
            roles.contains(Roles.ADMIN)
        )
    }
}