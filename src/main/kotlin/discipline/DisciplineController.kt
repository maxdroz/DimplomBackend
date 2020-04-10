package discipline

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import spark.Route

class DisciplineController {
    companion object {
        val fetchAllDisciplines = Route { req, res ->
            return@Route Gson().toJson(hashMapOf("disciplines" to Main.disciplineInteractor.getAll()))
        }
    }
}