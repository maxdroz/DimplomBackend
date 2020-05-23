package teacher

import com.google.gson.annotations.Expose
import common.Model

const val TEACHER_INVALID_ID = -1

data class Teacher(
    @Expose(serialize = false)
    val id: Int = TEACHER_INVALID_ID,
    val name: String = "",
    val surname: String = "",
    val patronymic: String = "",
    val phoneNumber: String = "",
    val description: String = ""
): Model<Teacher> {
    override fun trimmed(): Teacher {
        return copy(
                name = name.trim(),
                surname = surname.trim(),
                patronymic = patronymic.trim(),
                phoneNumber = phoneNumber.trim(),
                description = description.trim()
        )
    }
}