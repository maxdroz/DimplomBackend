import discipline.DisciplineController
import discipline.DisciplineInteractorNew
import group.GroupController
import group.GroupInteractorNew
import response.ResponseController
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import lesson.LessonController
import lesson.LessonInteractorNew
import office.OfficeController
import office.OfficeInteractorNew
import teacher.TeacherController
import teacher.TeacherInteractorNew
import utils.AddHeader
import utils.Path

fun main() {
    val app = Javalin.create { config ->
        config.enableDevLogging()
        config.enableCorsForAllOrigins()
    }.start(80)

//    app.before(LoginController.ensureLoginBeforeEditing)
//    app.options("*", OptionsController.optionsRequest)
    app.before {
        Thread.sleep(1000)
    }


    app.routes {
        path(Path.TEACHERS) {
            get(TeacherController.fetchAllTeachers)
            post(TeacherController.addTeacher)
            post("/check", TeacherController.canDeleteTeacher)
            path(":teacher-id") {
                get(TeacherController.getTeacher)
                put(TeacherController.editTeacher)
                delete(TeacherController.deleteTeacher)
            }
        }

        path(Path.DISCIPLINES) {
            get(DisciplineController.fetchAllDisciplines)
            post(DisciplineController.addDiscipline)
            post("/check", DisciplineController.canDeleteDiscipline)
            path(":discipline-id") {
                get(DisciplineController.getDiscipline)
                put(DisciplineController.editDiscipline)
                delete(DisciplineController.deleteDiscipline)
            }
        }

        path(Path.OFFICES) {
            get(OfficeController.fetchAllOffices)
            post(OfficeController.addOffice)
            post("/check", OfficeController.canDeleteOffice)
            path(":office-id") {
                get(OfficeController.getOffice)
                put(OfficeController.editOffice)
                delete(OfficeController.deleteOffice)
            }
        }

        path(Path.GROUPS) {
            get(GroupController.fetchAllGroups)
            post(GroupController.addGroup)
            post("/check", GroupController.canDeleteGroup)
            path(":group-id") {
                get(GroupController.getGroup)
                put(GroupController.editGroup)
                delete(GroupController.deleteGroup)
            }
        }

        path(Path.LESSONS) {
            get(LessonController.fetchAllLessons)
            post(LessonController.addLesson)
            post("/check", LessonController.canDeleteLesson)
            path(":lesson-id") {
                get(LessonController.getLesson)
                put(LessonController.editLesson)
                delete(LessonController.deleteLesson)
            }
        }

        path(Path.USER_API) {
            path(Path.TEACHERS) {
                get(TeacherController.getTeacher)
            }
            path(Path.LESSONS) {
                get(LessonController.fetchAllLessonsForUser)
            }
            path(Path.GROUPS) {
                get(GroupController.fetchAllGroupsUser)
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
    val disciplineInteractor = DisciplineInteractorNew(DB.conn)
    val teacherInteractor = TeacherInteractorNew(DB.conn)
    val lessonInteractor = LessonInteractorNew(DB.conn)
    val officeInteractor = OfficeInteractorNew(DB.conn)
    val groupInteractor = GroupInteractorNew(DB.conn)
}