import discipline.DisciplineController
import discipline.DisciplineInteractor
import lesson.LessonController
import lesson.LessonInteractor
import spark.Spark.*
import spark.kotlin.port
import teacher.TeacherContoller
import teacher.TeacherInteractor
import utils.AddJSONHeader

fun main() {
    port(80)

    post("/get/disciplines", DisciplineController.fetchAllDisciplines)
    post("/get/teachers", TeacherContoller.fetchAllTeachers)
    post("/get/lessons", LessonController.fetchAllLessons)

    after("*", AddJSONHeader.add)
}

class Main {
    companion object {
        val disciplineInteractor = DisciplineInteractor(DB.conn)
        val teacherInteractor = TeacherInteractor(DB.conn)
        val lessonInteractor = LessonInteractor(DB.conn)
    }
}