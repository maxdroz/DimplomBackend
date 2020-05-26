package login

import io.javalin.core.security.Role

enum class Roles : Role {
    ANYONE, WORKER, ADMIN
}