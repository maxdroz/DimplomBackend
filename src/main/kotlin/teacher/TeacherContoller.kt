package teacher

import com.google.gson.Gson
import spark.Route

class TeacherContoller {
    companion object {
        val fetchAllTeachers = Route { req, res ->
            return@Route Gson().toJson(hashMapOf("teachers" to Main.teacherInteractor.getAll()))
        }
    }
}