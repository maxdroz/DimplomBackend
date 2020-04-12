import discipline.DisciplineController
import discipline.DisciplineInteractor
import response.ResponseController
import io.javalin.Javalin
import lesson.LessonController
import lesson.LessonInteractor
import login.LoginController
import office.OfficeController
import office.OfficeInteractor
import teacher.TeacherController
import teacher.TeacherInteractor
import utils.AddJSONHeader
import utils.Path

fun main() {
    val app = Javalin.create().start(80)

    app.before(LoginController.ensureLoginBeforeEditing)

    app.get(Path.TEACHERS, TeacherController.fetchAllTeachers)
    app.get(Path.DISCIPLINES, DisciplineController.fetchAllDisciplines)
    app.get(Path.LESSONS, LessonController.fetchAllLessons)
    app.get(Path.OFFICES, OfficeController.fetchAllOffices)

    app.post(Path.TEACHERS, TeacherController.addTeacher)
    app.post(Path.DISCIPLINES, DisciplineController.addDiscipline)
    app.post(Path.OFFICES, OfficeController.addOffice)
    app.post(Path.LESSONS, LessonController.addLesson)

    app.patch(Path.TEACHERS, TeacherController.editTeacher)
    app.patch(Path.DISCIPLINES, DisciplineController.editDiscipline)
    app.patch(Path.OFFICES, OfficeController.editOffice)
    app.patch(Path.LESSONS, LessonController.editLesson)

    app.delete(Path.TEACHERS, TeacherController.deleteTeacher)
    app.delete(Path.DISCIPLINES, DisciplineController.deleteDiscipline)
    app.delete(Path.OFFICES, OfficeController.deleteOffice)
    app.delete(Path.LESSONS, LessonController.deleteLesson)

    //responses
    app.get(Path.LOGIN_REQUIRED, ResponseController.loginRequiredError)
    app.get(Path.SUCCESS, ResponseController.success)
    app.get(Path.ERROR, ResponseController.error)

    app.after(AddJSONHeader.add)
}

object Main {
    val disciplineInteractor = DisciplineInteractor(DB.conn)
    val teacherInteractor = TeacherInteractor(DB.conn)
    val lessonInteractor = LessonInteractor(DB.conn)
    val officeInteractor = OfficeInteractor(DB.conn)
}