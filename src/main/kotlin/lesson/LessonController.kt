package lesson

import com.google.gson.*
import com.google.gson.annotations.Expose
import common.getLessonFilter
import discipline.Discipline
import group.Group
import io.javalin.http.Context
import io.javalin.http.Handler
import office.Office
import teacher.Teacher
import utils.getAllParams
import java.sql.Timestamp
import java.util.*

object LessonController {
    private val userGson by lazy {
        val builder = GsonBuilder().addSerializationExclusionStrategy(object : ExclusionStrategy {
            override fun shouldSkipClass(clazz: Class<*>): Boolean {
                return false
            }

            override fun shouldSkipField(f: FieldAttributes): Boolean {
                return f.getAnnotation(Expose::class.java)?.serialize == false
            }

        })
        val timestampSerializer = JsonSerializer<Timestamp> { src, _, _ ->
            return@JsonSerializer JsonPrimitive(src.toInstant().toEpochMilli())
        }
        val disciplineSerializer = JsonSerializer<Discipline> { src, _, _ ->
            return@JsonSerializer JsonPrimitive(src.name)
        }
        val officeSerializer = JsonSerializer<Office> { src, _, _ ->
            return@JsonSerializer JsonPrimitive(src.office)
        }
        val groupSerializer = JsonSerializer<Group> { src, _, _ ->
            return@JsonSerializer JsonPrimitive(src.name)
        }
        val timestampDeserializer = JsonDeserializer { src, typeOfSrc, context ->
            return@JsonDeserializer if (src.asJsonPrimitive.isNumber) {
                Timestamp(src.asLong)
            } else {
                val str = src.asString.replace('T', ' ').removeSuffix("Z")
                println(str)
                Timestamp(Timestamp.valueOf(str).time + TimeZone.getDefault().rawOffset)
            }
        }
        val disciplineDeserializer = JsonDeserializer { src, typeOfSrc, context ->
            return@JsonDeserializer Discipline(src.asInt)
        }
        val officeDeserializer = JsonDeserializer { src, typeOfSrc, context ->
            return@JsonDeserializer Office(src.asInt)
        }
        val teacherDeserializer = JsonDeserializer { src, typeOfSrc, context ->
            return@JsonDeserializer Teacher(src.asInt)
        }
        val groupDeserializer = JsonDeserializer { src, typeOfSrc, context ->
            return@JsonDeserializer Group(src.asInt)
        }
        builder.registerTypeAdapter(Timestamp::class.java, timestampDeserializer)
        builder.registerTypeAdapter(Discipline::class.java, disciplineDeserializer)
        builder.registerTypeAdapter(Office::class.java, officeDeserializer)
        builder.registerTypeAdapter(Teacher::class.java, teacherDeserializer)
        builder.registerTypeAdapter(Group::class.java, groupDeserializer)
        builder.registerTypeAdapter(Timestamp::class.java, timestampSerializer)
        builder.registerTypeAdapter(Discipline::class.java, disciplineSerializer)
        builder.registerTypeAdapter(Office::class.java, officeSerializer)
        builder.registerTypeAdapter(Group::class.java, groupSerializer)
        builder.create()
    }

    private val adminGson by lazy {
        val builder = GsonBuilder()
        val timestampSerializer = JsonSerializer<Timestamp> { src, _, _ ->
            return@JsonSerializer JsonPrimitive(src.toInstant().toEpochMilli())
        }
        val disciplineSerializer = JsonSerializer<Discipline> { src, _, _ ->
            return@JsonSerializer JsonPrimitive(src.id)
        }
        val officeSerializer = JsonSerializer<Office> { src, _, _ ->
            return@JsonSerializer JsonPrimitive(src.id)
        }
        val teacherSerializer = JsonSerializer<Teacher> { src, _, _ ->
            return@JsonSerializer JsonPrimitive(src.id)
        }
        val groupSerializer = JsonSerializer<Group> { src, _, _ ->
            return@JsonSerializer JsonPrimitive(src.id)
        }
        builder.registerTypeAdapter(Timestamp::class.java, timestampSerializer)
        builder.registerTypeAdapter(Discipline::class.java, disciplineSerializer)
        builder.registerTypeAdapter(Office::class.java, officeSerializer)
        builder.registerTypeAdapter(Teacher::class.java, teacherSerializer)
        builder.registerTypeAdapter(Group::class.java, groupSerializer)
        return@lazy builder.create()
    }

    val fetchAllLessonsForUser = Handler { ctx ->
        val from: Long? = ctx.req.getParameter("from")?.toLong()
        val to: Long? = ctx.req.getParameter("to")?.toLong()
        ctx.html(userGson.toJson(Main.lessonInteractor.getFiltered(from, to)))
    }

    val fetchAllLessons = Handler { ctx ->
        ctx.html(adminGson.toJson(Main.lessonInteractor.getAll(ctx.getAllParams(), ctx.getLessonFilter())))
    }

    val getLesson = Handler { ctx ->
        ctx.html(adminGson.toJson(Main.lessonInteractor.get(ctx.getParamId())))
    }

    val addLesson = Handler { ctx ->
        val data = userGson.fromJson(ctx.body(), Lesson::class.java)
        ctx.html(adminGson.toJson(Main.lessonInteractor.add(data)))
    }

    val editLesson = Handler { ctx ->
        val data = userGson.fromJson(ctx.body(), Lesson::class.java).copy(id = ctx.getParamId())
        ctx.html(adminGson.toJson(Main.lessonInteractor.edit(data)))
    }

    val deleteLesson = Handler { ctx ->
        ctx.html(adminGson.toJson(Main.lessonInteractor.delete(ctx.getParamId())))
    }

    val canDeleteLesson = Handler { ctx ->
        ctx.json(mapOf("result" to "success"))
    }

    private fun Context.getParamId(): Int {
        return pathParam(":lesson-id").toIntOrNull() ?: LESSON_INVALID_ID
    }
}