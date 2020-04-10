package lesson

import com.google.gson.annotations.Expose
import discipline.Discipline
import office.Office
import teacher.Teacher
import java.sql.Timestamp

data class Lesson(
    @Expose(serialize = false)
    val id: Int,
    val startTime: Timestamp,
    val endTime: Timestamp,
    val discipline: Discipline,
    val teacher: Teacher,
    val office: Office
)