import discipline.DisciplineController
import discipline.DisciplineInteractorNew
import group.GroupController
import group.GroupInteractorNew
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.security.SecurityUtil.roles
import lesson.LessonController
import lesson.LessonInteractorNew
import login.LoginController
import login.Roles
import office.OfficeController
import office.OfficeInteractorNew
import response.ResponseController
import teacher.TeacherController
import teacher.TeacherInteractorNew
import users.UserController
import users.UserInteractor
import utils.AddHeader
import utils.Path

fun main() {

    val app = Javalin.create { config ->
        config.enableDevLogging()
        config.enableCorsForAllOrigins()
        config.accessManager(LoginController.accessManager)
    }.start(getHerokuAssignedPort())

//    app.before(LoginController.ensureLoginBeforeEditing)
//    app.options("*", OptionsController.optionsRequest)
    app.before {
//        Thread.sleep(3000)
    }

    DB.conn

    app.routes {
        path(Path.TEACHERS) {
            get(TeacherController.fetchAllTeachers, roles(Roles.WORKER, Roles.ADMIN))
            post(TeacherController.addTeacher, roles(Roles.WORKER, Roles.ADMIN))
            post("/check", TeacherController.canDeleteTeacher, roles(Roles.WORKER, Roles.ADMIN))
            path(":teacher-id") {
                get(TeacherController.getTeacher, roles(Roles.WORKER, Roles.ADMIN))
                put(TeacherController.editTeacher, roles(Roles.WORKER, Roles.ADMIN))
                delete(TeacherController.deleteTeacher, roles(Roles.WORKER, Roles.ADMIN))
            }
        }

        path(Path.DISCIPLINES) {
            get(DisciplineController.fetchAllDisciplines, roles(Roles.WORKER, Roles.ADMIN))
            post(DisciplineController.addDiscipline, roles(Roles.WORKER, Roles.ADMIN))
            post("/check", DisciplineController.canDeleteDiscipline, roles(Roles.WORKER, Roles.ADMIN))
            path(":discipline-id") {
                get(DisciplineController.getDiscipline, roles(Roles.WORKER, Roles.ADMIN))
                put(DisciplineController.editDiscipline, roles(Roles.WORKER, Roles.ADMIN))
                delete(DisciplineController.deleteDiscipline, roles(Roles.WORKER, Roles.ADMIN))
            }
        }

        path(Path.OFFICES) {
            get(OfficeController.fetchAllOffices, roles(Roles.WORKER, Roles.ADMIN))
            post(OfficeController.addOffice, roles(Roles.WORKER, Roles.ADMIN))
            post("/check", OfficeController.canDeleteOffice, roles(Roles.WORKER, Roles.ADMIN))
            path(":office-id") {
                get(OfficeController.getOffice, roles(Roles.WORKER, Roles.ADMIN))
                put(OfficeController.editOffice, roles(Roles.WORKER, Roles.ADMIN))
                delete(OfficeController.deleteOffice, roles(Roles.WORKER, Roles.ADMIN))
            }
        }

        path(Path.GROUPS) {
            get(GroupController.fetchAllGroups, roles(Roles.WORKER, Roles.ADMIN))
            post(GroupController.addGroup, roles(Roles.WORKER, Roles.ADMIN))
            post("/check", GroupController.canDeleteGroup, roles(Roles.WORKER, Roles.ADMIN))
            path(":group-id") {
                get(GroupController.getGroup, roles(Roles.WORKER, Roles.ADMIN))
                put(GroupController.editGroup, roles(Roles.WORKER, Roles.ADMIN))
                delete(GroupController.deleteGroup, roles(Roles.WORKER, Roles.ADMIN))
            }
        }

        path(Path.LESSONS) {
            get(LessonController.fetchAllLessons, roles(Roles.WORKER, Roles.ADMIN))
            post(LessonController.addLesson, roles(Roles.WORKER, Roles.ADMIN))
            post("/check", LessonController.canDeleteLesson, roles(Roles.WORKER, Roles.ADMIN))
            path(":lesson-id") {
                get(LessonController.getLesson, roles(Roles.WORKER, Roles.ADMIN))
                put(LessonController.editLesson, roles(Roles.WORKER, Roles.ADMIN))
                delete(LessonController.deleteLesson, roles(Roles.WORKER, Roles.ADMIN))
            }
        }

        path(Path.USERS) {
            get(UserController.fetchAllUsers, roles(Roles.ADMIN))
            post(UserController.addUser, roles(Roles.ADMIN))
            path(":username") {
                get(UserController.getUser, roles(Roles.ADMIN))
                put(UserController.editUser, roles(Roles.ADMIN))
                delete(UserController.deleteUser, roles(Roles.ADMIN))
            }
            path("isUsernameTaken") {
                post(UserController.isUsernameTaken, roles(Roles.ADMIN))
            }
        }

        path("changePassword") {
            post(UserController.updatePassword, roles(Roles.WORKER, Roles.ADMIN))
        }

        path(Path.AUTHORIZE) {
            post(UserController.authorizeCallback, roles(Roles.ANYONE))
        }

        path(Path.USER_API) {
            path(Path.TEACHERS) {
                get(TeacherController.fetchAllTeachers, roles(Roles.ANYONE))
            }
            path(Path.LESSONS) {
                get(LessonController.fetchAllLessonsForUser, roles(Roles.ANYONE))
            }
            path(Path.GROUPS) {
                get(GroupController.fetchAllGroupsUser, roles(Roles.ANYONE))
            }
        }
    }

    //responses
    app.get(Path.LOGIN_REQUIRED, ResponseController.loginRequiredError)
    app.get(Path.SUCCESS, ResponseController.success)
    app.get(Path.ERROR, ResponseController.error)

    app.after(AddHeader.addJSONHeader)
    app.after(AddHeader.addCrossOriginHeader)
}

private fun getHerokuAssignedPort(): Int {
    val herokuPort = System.getenv("PORT")
    return herokuPort?.toInt() ?: 80
}

object Main {
    val disciplineInteractor = DisciplineInteractorNew(DB.conn)
    val teacherInteractor = TeacherInteractorNew(DB.conn)
    val lessonInteractor = LessonInteractorNew(DB.conn)
    val officeInteractor = OfficeInteractorNew(DB.conn)
    val groupInteractor = GroupInteractorNew(DB.conn)
    val userInteractor = UserInteractor(DB.conn)
}