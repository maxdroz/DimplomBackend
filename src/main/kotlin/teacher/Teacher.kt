package teacher

import com.google.gson.annotations.Expose

data class Teacher(
    @Expose(serialize = false)
    val id: Int,
    val name: String,
    val surname: String,
    val patronymic: String,
    val phoneNumber: String,
    val description: String
)