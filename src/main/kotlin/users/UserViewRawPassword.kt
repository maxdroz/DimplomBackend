package users

import login.Roles

data class UserViewRawPassword(
    val id: String,
    val password: String,
    val canEditUsers: Boolean
) {
    fun toUserModel(): UserModel {
        val roles = if(canEditUsers) {
            setOf(Roles.ADMIN)
        } else {
            setOf(Roles.WORKER)
        }

        return UserModel(
            id,
            password.hashSHA256(),
            roles
        )
    }
}