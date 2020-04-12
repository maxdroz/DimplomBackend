package teacher

import com.google.gson.annotations.Expose

const val TEACHER_INVALID_ID = -1

data class Teacher(
    @Expose(serialize = false)
    val id: Int = TEACHER_INVALID_ID,
    val name: String = "",
    val surname: String = "",
    val patronymic: String = "",
    val phoneNumber: String = "",
    val description: String = ""
)