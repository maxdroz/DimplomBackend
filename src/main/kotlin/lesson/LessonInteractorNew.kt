package lesson

import common.Filter
import common.Interactor
import common.LessonFilter
import discipline.Discipline
import group.Group
import office.Office
import teacher.Teacher
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class LessonInteractorNew(private val connection: Connection) : Interactor<Lesson, LessonFilter>(connection) {
    override val tableParams: String
        get() = "*"
    override val tableName: String
        get() = "lesson"
    override val insertQuery: String
        get() = "INSERT INTO lesson VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)"
    override val editQuery: String
        get() = "UPDATE lesson SET start_time = ?, end_time = ?, id_discipline = ?, id_teacher = ?, id_office = ?, id_group = ? WHERE id = ?"
    override val referenceName: String
        get() = ""

    override fun getSearchPathQuery(filter: LessonFilter): String {
        val teacher = if(filter.teacher != null) "AND id_teacher = ?" else ""
        val office = if(filter.office != null) "AND id_office = ?" else ""
        val group = if(filter.group != null) "AND id_group = ?" else ""
        val discipline = if(filter.discipline != null) "AND id_discipline = ?" else ""
        val q = if(filter.q != null) "AND CONCAT(id) LIKE ?" else ""
        return "WHERE 1 = 1 $teacher $office $group $discipline $q"
    }

    @Suppress("UNUSED_CHANGED_VALUE")
    override fun addParamsToQueryForSearch(st: PreparedStatement, filter: LessonFilter) {
        var id = 1
        if(filter.teacher != null) st.setInt(id++, filter.teacher)
        if(filter.office != null) st.setInt(id++, filter.office)
        if(filter.group != null) st.setInt(id++, filter.group)
        if(filter.discipline != null) st.setInt(id++, filter.discipline)
        if(filter.q != null) st.setString(id++, filter.q)
    }

    override fun addParamsToQueryForInsert(st: PreparedStatement, obj: Lesson) {
        st.setTimestamp(1, obj.startTime)
        st.setTimestamp(2, obj.endTime)
        st.setInt(3, obj.discipline.id)
        st.setInt(4, obj.teacher.id)
        st.setInt(5, obj.office.id)
        st.setInt(6, obj.group.id)
    }

    override fun addParamsToQueryForEdit(st: PreparedStatement, obj: Lesson) {
        st.setTimestamp(1, obj.startTime)
        st.setTimestamp(2, obj.endTime)
        st.setInt(3, obj.discipline.id)
        st.setInt(4, obj.teacher.id)
        st.setInt(5, obj.office.id)
        st.setInt(6, obj.group.id)
        st.setInt(7, obj.id)
    }

    override fun ResultSet.getObject(): Lesson {
        val id = getInt("id")
        val startTime = getTimestamp("start_time")
        val endTime = getTimestamp("end_time")
        val discipline = Discipline(getInt("id_discipline"))
        val teacher = Teacher(getInt("id_teacher"))
        val office = Office(getInt("id_office"))
        val group = Group(getInt("id_group"))
        return Lesson(id, startTime, endTime, discipline, teacher, office, group)
    }

    override fun String.toDBName(): String {
        return when (this.toLowerCase()) {
            "starttime" -> "start_time"
            "endtime" -> "end_time"
            "teacher" -> "id_teacher"
            "discipline" -> "id_discipline"
            "office" -> "id_office"
            "group" -> "id_group"
            else -> this
        }
    }

    fun getFiltered(from: Long?, to: Long?): List<Lesson> {
        val whereStatement = when {
            from != null && to == null -> "WHERE start_time >= TO_TIMESTAMP(? / 1000)"
            from == null && to != null -> "WHERE end_time <= TO_TIMESTAMP(? / 1000)"
            from != null && to != null -> "WHERE start_time >= TO_TIMESTAMP(? / 1000) AND end_time <= TO_TIMESTAMP(? / 1000)"
            else -> ""
        }
        val query =
            "SELECT lesson.id, start_time, end_time, id_discipline, id_teacher, id_office, id_group, \"group\".name as group_name, discipline.name, teacher.name as teacher_name, surname, patronymic, phone_number, description, office, discipline.short_name " +
                    "FROM lesson " +
                    "INNER JOIN discipline ON lesson.id_discipline = discipline.id " +
                    "INNER JOIN teacher ON lesson.id_teacher = teacher.id " +
                    "INNER JOIN office ON lesson.id_office = office.id " +
                    "INNER JOIN \"group\" ON lesson.id_group = \"group\".id " + whereStatement + " ORDER BY start_time "
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
        while (res.next()) {
            ans.add(res.getLessonFull())
        }
        return ans
    }

    fun checkIfCouldInsertLesson(lesson: Lesson): String? {
        val otherLessonOfTeacher = checkTeacherTimelineCollisions(lesson)
        if(otherLessonOfTeacher != null) {
            return "Пара с id $otherLessonOfTeacher уже идет у       этого преподавателя"
        }
        val otherLessonOfOffice = checkOfficeTimelineCollisions(lesson)
        if(otherLessonOfOffice != null) {
            return "Пара с id $otherLessonOfOffice уже идет в этой аудитории"
        }
        val otherLessonOfGroup = checkGroupTimelineCollisions(lesson)
        if(otherLessonOfGroup != null) {
            return "Пара с id $otherLessonOfGroup уже идет у этой группы"
        }
        //group has other class
        //office is taken
        //teacher has other class
        return null
    }

    private fun checkGroupTimelineCollisions(lesson: Lesson): Int? { //lesson with error
        val query = "SELECT lesson.id FROM lesson INNER JOIN \"group\" ON \"group\".id = lesson.id_group WHERE id_group = ? AND start_time < ? AND end_time > ? LIMIT 1"
        val st = connection.prepareStatement(query)
        st.setInt(1, lesson.group.id)
        st.setTimestamp(2, lesson.endTime)
        st.setTimestamp(3, lesson.startTime)
        val res = st.executeQuery()
        if (res.next()) {
            return res.getInt(1)
        }
        return null
    }

    private fun checkOfficeTimelineCollisions(lesson: Lesson): Int? { //lesson with error
        val query = "SELECT lesson.id FROM lesson INNER JOIN office ON office.id = lesson.id_office WHERE id_office = ? AND start_time < ? AND end_time > ? LIMIT 1"
        val st = connection.prepareStatement(query)
        st.setInt(1, lesson.office.id)
        st.setTimestamp(2, lesson.endTime)
        st.setTimestamp(3, lesson.startTime)
        val res = st.executeQuery()
        if (res.next()) {
            return res.getInt(1)
        }
        return null
    }

    private fun checkTeacherTimelineCollisions(lesson: Lesson): Int? { //lesson with error
        val query = "SELECT lesson.id FROM lesson INNER JOIN teacher ON teacher.id = lesson.id_teacher WHERE id_teacher = ? AND start_time < ? AND end_time > ? LIMIT 1"
        val st = connection.prepareStatement(query)
        st.setInt(1, lesson.teacher.id)
        st.setTimestamp(2, lesson.endTime)
        st.setTimestamp(3, lesson.startTime)
        val res = st.executeQuery()
        if (res.next()) {
            return res.getInt(1)
        }
        return null
    }

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

    private fun ResultSet.getTeacher(): Teacher {
        val id = getInt("id_teacher")
        val name = getString("teacher_name")
        val surname = getString("surname")
        val patronymic = getString("patronymic")
        val phoneNumber = getString("phone_number")
        val description = getString("description")
        return Teacher(id, name, surname, patronymic, phoneNumber, description)
    }

    private fun ResultSet.getGroup(): Group {
        val id = getInt("id_group")
        val group = getString("group_name")
        return Group(id, group)
    }

    private fun ResultSet.getDiscipline(): Discipline {
        val id = getInt("id_discipline")
        val name = getString("name")
        val shortName = getString("short_name")
        return Discipline(id, name, shortName)
    }

    private fun ResultSet.getOffice(): Office {
        val id = getInt("id_office")
        val office = getString("office")
        return Office(id, office)
    }
}
