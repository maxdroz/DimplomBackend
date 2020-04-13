import discipline.DisciplineController
import discipline.DisciplineInteractor
import group.GroupController
import group.GroupInteractor
import response.ResponseController
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import lesson.LessonController
import lesson.LessonInteractor
import office.OfficeController
import office.OfficeInteractor
import teacher.TeacherController
import teacher.TeacherInteractor
import utils.AddHeader
import utils.Path

fun main() {
    val app = Javalin.create().start(80)

//    app.before(LoginController.ensureLoginBeforeEditing)

    app.routes {
        path(Path.TEACHERS) {
            get(TeacherController.fetchAllTeachers)
            post(TeacherController.addTeacher)
            path(":teacher-id") {
                get(TeacherController.getTeacher)
                put(TeacherController.editTeacher)
                delete(TeacherController.deleteTeacher)
            }
        }

        path(Path.DISCIPLINES) {
            get(DisciplineController.fetchAllDisciplines)
            post(DisciplineController.addDiscipline)
            path(":discipline-id") {
                get(DisciplineController.getDiscipline)
                put(DisciplineController.editDiscipline)
                delete(DisciplineController.deleteDiscipline)
            }
        }

        path(Path.OFFICES) {
            get(OfficeController.fetchAllOffices)
            post(OfficeController.addOffice)
            path(":office-id") {
                get(OfficeController.getOffice)
                put(OfficeController.editOffice)
                delete(OfficeController.deleteOffice)
            }
        }

        path(Path.LESSONS) {
            get(LessonController.fetchAllLessons)
            post(LessonController.addLesson)
            path(":lesson-id") {
                get(LessonController.getLesson)
                put(LessonController.editLesson)
                delete(LessonController.deleteLesson)
            }
        }

        path(Path.GROUPS) {
            get(GroupController.fetchAllGroups)
            post(GroupController.addGroup)
            path(":group-id") {
                get(GroupController.getGroup)
                put(GroupController.editGroup)
                delete(GroupController.deleteGroup)
            }
        }

        path(Path.USER_API) {
            path(Path.TEACHERS) {
                get(TeacherController.getTeacher)
            }
            path(Path.LESSONS) {
                get(LessonController.fetchAllLessonsForUser)
            }
        }
    }

    //responses
    app.get(Path.LOGIN_REQUIRED, ResponseController.loginRequiredError)
    app.get(Path.SUCCESS, ResponseController.success)
    app.get(Path.ERROR, ResponseController.error)

    app.after(AddHeader.addJSONHeader)
    app.after(AddHeader.addCrossOriginHeader)
    app.after(AddHeader.addXTotalCountHeader)
}

object Main {
    val disciplineInteractor = DisciplineInteractor(DB.conn)
    val teacherInteractor = TeacherInteractor(DB.conn)
    val lessonInteractor = LessonInteractor(DB.conn)
    val officeInteractor = OfficeInteractor(DB.conn)
    val groupInteractor = GroupInteractor(DB.conn)
}