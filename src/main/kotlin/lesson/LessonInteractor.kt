package lesson

import discipline.Discipline
import discipline.DisciplineInteractor.Companion.getDiscipline
import office.OfficeInteractor.Companion.getOffice
import teacher.TeacherInteractor.Companion.getTeacher
import java.sql.Connection
import java.sql.ResultSet

class LessonInteractor(private val connection: Connection) {
    fun getFiltered(from: Long?, to: Long?): List<Lesson> {
        val whereStatement = when {
            from != null && to == null -> "WHERE start_time >= TO_TIMESTAMP($from / 1000)"
            from == null && to != null -> "WHERE end_time <= TO_TIMESTAMP($to / 1000)"
            from != null && to != null -> "WHERE start_time >= TO_TIMESTAMP($from / 1000) AND end_time <= TO_TIMESTAMP($to / 1000)"
            else -> ""
        }
        val st = connection.createStatement()
        val res = st.executeQuery(
            "SELECT lesson.id, start_time, end_time, id_discipline, id_teacher, id_office, discipline.name, teacher.name as teacher_name, surname, patronymic, phone_number, description, office " +
                    "FROM lesson " +
                    "INNER JOIN discipline ON lesson.id_discipline = discipline.id " +
                    "INNER JOIN teacher ON lesson.id_teacher = teacher.id " +
                    "INNER JOIN office ON lesson.id_office = office.id " + whereStatement)
        val ans = mutableListOf<Lesson>()
        while(res.next()){
            ans.add(res.getLesson())
        }
        return ans
    }

    companion object {
        private fun ResultSet.getLesson(): Lesson {
            val id = getInt("id")
            val startTime = getTimestamp("start_time")
            val endTime = getTimestamp("end_time")
            val discipline = getDiscipline()
            val teacher = getTeacher()
            val office = getOffice()
            return Lesson(id, startTime, endTime, discipline, teacher, office)
        }
    }
}