package users

import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureDSA
import login.Roles
import sun.security.provider.SHA

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