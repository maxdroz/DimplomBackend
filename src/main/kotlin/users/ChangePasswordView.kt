package users

data class ChangePasswordView (
    val oldPassword: String,
    val password: String
)