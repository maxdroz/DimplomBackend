package users

import login.Roles

data class UserView(
    val id: String,
    val hashedPassword: String,
    val canEditUsers: Boolean = false
) {
    fun toUserViewRawPassword(): UserViewRawPassword {
        return UserViewRawPassword(id, hashedPassword, canEditUsers)
    }

    fun toUserModel(): UserModel {
        val roles = if(canEditUsers) {
            setOf(Roles.ADMIN)
        } else {
            setOf(Roles.WORKER)
        }
        return UserModel(
            id, hashedPassword, roles
        )
    }
}