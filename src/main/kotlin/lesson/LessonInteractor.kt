package lesson

import discipline.Discipline
import discipline.DisciplineInteractor.Companion.getDiscipline
import office.OfficeInteractor.Companion.getOffice
import org.slf4j.LoggerFactory
import teacher.TeacherInteractor.Companion.getTeacher
import java.lang.Exception
import java.sql.Connection
import java.sql.Ref
import java.sql.ResultSet

class LessonInteractor(private val connection: Connection) {
    fun getFiltered(from: Long?, to: Long?): List<Lesson> {
        val whereStatement = when {
            from != null && to == null -> "WHERE start_time >= TO_TIMESTAMP(? / 1000)"
            from == null && to != null -> "WHERE end_time <= TO_TIMESTAMP(? / 1000)"
            from != null && to != null -> "WHERE start_time >= TO_TIMESTAMP(? / 1000) AND end_time <= TO_TIMESTAMP(? / 1000)"
            else -> ""
        }
        val query = "SELECT lesson.id, start_time, end_time, id_discipline, id_teacher, id_office, discipline.name, teacher.name as teacher_name, surname, patronymic, phone_number, description, office " +
                "FROM lesson " +
                "INNER JOIN discipline ON lesson.id_discipline = discipline.id " +
                "INNER JOIN teacher ON lesson.id_teacher = teacher.id " +
                "INNER JOIN office ON lesson.id_office = office.id " + whereStatement
        val st = connection.prepareStatement(query)
        when {
            from != null && to == null -> st.setLong(1, from)
            from == null && to != null -> st.setLong(1, to)
            from != null && to != null -> {
                st.setLong(1, from)
                st.setLong(2, to)
            }
        }
        val res = st.executeQuery()
        val ans = mutableListOf<Lesson>()
        while(res.next()){
            ans.add(res.getLesson())
        }
        return ans
    }

    fun add(lesson: Lesson): String? {
        val st = connection.prepareStatement("INSERT INTO lesson VALUES (DEFAULT, ?, ?, ?, ?, ?)")
        st.setTimestamp(1, lesson.startTime)
        st.setTimestamp(2, lesson.endTime)
        st.setInt(3, lesson.discipline.id)
        st.setInt(4, lesson.teacher.id)
        st.setInt(5, lesson.office.id)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
    }

    fun edit(lesson: Lesson): String? {
        val st = connection.prepareStatement("UPDATE lesson SET start_time = ?, end_time = ?, id_discipline = ?, id_teacher = ?, id_office = ? WHERE id = ?")
        st.setTimestamp(1, lesson.startTime)
        st.setTimestamp(2, lesson.endTime)
        st.setInt(3, lesson.discipline.id)
        st.setInt(4, lesson.teacher.id)
        st.setInt(5, lesson.office.id)
        st.setInt(6, lesson.id)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
    }

    fun delete(id: Int): String? {
        val st = connection.prepareStatement("DELETE FROM lesson WHERE id = ?")
        st.setInt(1, id)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
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