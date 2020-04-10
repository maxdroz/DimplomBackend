package lesson

import com.google.gson.*
import com.google.gson.annotations.Expose
import discipline.Discipline
import office.Office
import spark.Route
import java.sql.Timestamp
import java.time.temporal.TemporalField

class LessonController {
    companion object {
        val gson by lazy {
            val builder = GsonBuilder().addSerializationExclusionStrategy(object: ExclusionStrategy{
                override fun shouldSkipClass(clazz: Class<*>): Boolean {
                    return false
                }

                override fun shouldSkipField(f: FieldAttributes): Boolean {
                    return f.getAnnotation(Expose::class.java)?.serialize == false
                }

            })
            val timestampSerializer = JsonSerializer<Timestamp> { src, typeOfSrc, context ->
                return@JsonSerializer JsonPrimitive(src.toInstant().toEpochMilli())
            }
            val disciplineSerializer = JsonSerializer<Discipline> { src, typeOfSrc, context ->
                return@JsonSerializer JsonPrimitive(src.name)
            }
            val officeSerializer = JsonSerializer<Office> { src, typeOfSrc, context ->
                return@JsonSerializer JsonPrimitive(src.office)
            }
            builder.registerTypeAdapter(Timestamp::class.java, timestampSerializer)
            builder.registerTypeAdapter(Discipline::class.java, disciplineSerializer)
            builder.registerTypeAdapter(Office::class.java, officeSerializer)
            builder.create()
        }

        val fetchAllLessons = Route { req, res ->
            val from: Long? = req.queryParams("from")?.toLong()
            val to: Long? = req.queryParams("to")?.toLong()
            return@Route gson.toJson(hashMapOf("lessons" to Main.lessonInteractor.getFiltered(from, to)))
        }
    }
}