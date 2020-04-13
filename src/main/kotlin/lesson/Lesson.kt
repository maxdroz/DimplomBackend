package lesson

import com.google.gson.annotations.Expose
import discipline.Discipline
import group.Group
import office.Office
import teacher.Teacher
import java.sql.Time
import java.sql.Timestamp

const val LESSON_INVALID_ID = -1
val LESSON_INVALID_TIMESTAMP = Timestamp(0)

data class Lesson(
    val id: Int = LESSON_INVALID_ID,
    val startTime: Timestamp = LESSON_INVALID_TIMESTAMP,
    val endTime: Timestamp = LESSON_INVALID_TIMESTAMP,
    val discipline: Discipline = Discipline(),
    val teacher: Teacher = Teacher(),
    val office: Office = Office(),
    val group: Group = Group()
)