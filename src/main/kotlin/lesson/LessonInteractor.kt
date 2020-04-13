package lesson

import discipline.Discipline
import discipline.DisciplineInteractor.Companion.getDiscipline
import group.Group
import group.GroupInteractor.Companion.getGroup
import office.Office
import office.OfficeInteractor.Companion.getOffice
import org.slf4j.LoggerFactory
import teacher.Teacher
import teacher.TeacherInteractor.Companion.getTeacher
import java.lang.Exception
import java.sql.Connection
import java.sql.ResultSet

class LessonInteractor(private val connection: Connection) {
    fun getFiltered(from: Long?, to: Long?): List<Lesson> {
        val whereStatement = when {
            from != null && to == null -> "WHERE start_time >= TO_TIMESTAMP(? / 1000)"
            from == null && to != null -> "WHERE end_time <= TO_TIMESTAMP(? / 1000)"
            from != null && to != null -> "WHERE start_time >= TO_TIMESTAMP(? / 1000) AND end_time <= TO_TIMESTAMP(? / 1000)"
            else -> ""
        }
        val query = "SELECT lesson.id, start_time, end_time, id_discipline, id_teacher, id_office, id_group, \"group\".name as group_name, discipline.name, teacher.name as teacher_name, surname, patronymic, phone_number, description, office " +
                "FROM lesson " +
                "INNER JOIN discipline ON lesson.id_discipline = discipline.id " +
                "INNER JOIN teacher ON lesson.id_teacher = teacher.id " +
                "INNER JOIN office ON lesson.id_office = office.id " +
                "INNER JOIN \"group\" ON lesson.id_group = \"group\".id " + whereStatement
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
            ans.add(res.getLessonFull())
        }
        return ans
    }

    fun getAll(): List<Lesson> {
        val query = "SELECT * FROM lesson LIMIT 10"
        val st = connection.createStatement()
        val res = st.executeQuery(query)
        val ans = mutableListOf<Lesson>()
        while(res.next()){
            ans.add(res.getLessonIdsOnly())
        }
        return ans
    }

    fun get(id: Int): Lesson {
        val st = connection.prepareStatement("SELECT * FROM lesson WHERE id = ?")
        st.setInt(1, id)
        val res = st.executeQuery()
        res.next()
        return res.getLessonIdsOnly()
    }

    fun add(lesson: Lesson): String? {
        val st = connection.prepareStatement("INSERT INTO lesson VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)")
        st.setTimestamp(1, lesson.startTime)
        st.setTimestamp(2, lesson.endTime)
        st.setInt(3, lesson.discipline.id)
        st.setInt(4, lesson.teacher.id)
        st.setInt(5, lesson.office.id)
        st.setInt(6, lesson.group.id)
        return try {
            st.execute()
            null
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error("", e)
            e.message
        }
    }

    fun edit(lesson: Lesson): String? {
        val st = connection.prepareStatement("UPDATE lesson SET start_time = ?, end_time = ?, id_discipline = ?, id_teacher = ?, id_office = ?, id_group = ? WHERE id = ?")
        st.setTimestamp(1, lesson.startTime)
        st.setTimestamp(2, lesson.endTime)
        st.setInt(3, lesson.discipline.id)
        st.setInt(4, lesson.teacher.id)
        st.setInt(5, lesson.office.id)
        st.setInt(6, lesson.group.id)
        st.setInt(7, lesson.id)
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
        private fun ResultSet.getLessonFull(): Lesson {
            val id = getInt("id")
            val startTime = getTimestamp("start_time")
            val endTime = getTimestamp("end_time")
            val discipline = getDiscipline()
            val teacher = getTeacher()
            val office = getOffice()
            val group = getGroup()
            return Lesson(id, startTime, endTime, discipline, teacher, office, group)
        }

        private fun ResultSet.getLessonIdsOnly(): Lesson {
            val id = getInt("id")
            val startTime = getTimestamp("start_time")
            val endTime = getTimestamp("end_time")
            val discipline = Discipline(getInt("id_discipline"))
            val teacher = Teacher(getInt("id_teacher"))
            val office = Office(getInt("id_office"))
            val group = Group(getInt("id_group"))
            return Lesson(id, startTime, endTime, discipline, teacher, office, group)
        }
    }
}