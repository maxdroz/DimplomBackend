package lesson

import com.google.gson.*
import com.google.gson.annotations.Expose
import discipline.DISCIPLINE_INVALID_ID
import discipline.Discipline
import group.GROUP_INVALID_ID
import group.Group
import io.javalin.http.Context
import io.javalin.http.Handler
import office.Office
import response.showError
import teacher.TEACHER_INVALID_ID
import teacher.Teacher
import utils.Path
import utils.ResponseGenerator
import java.sql.Timestamp

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
            return@JsonDeserializer Timestamp(src.asLong)
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
        ctx.html(adminGson.toJson(Main.lessonInteractor.getAll()))
    }

    val getLesson = Handler { ctx ->
        ctx.html(adminGson.toJson(Main.lessonInteractor.get(ctx.getParamId())))
    }

    val addLesson = Handler { ctx ->
        ResponseGenerator.generate(ctx, Lesson::class.java, userGson,
            dataFormatWrong = {
                it.discipline.id == DISCIPLINE_INVALID_ID ||
                        it.office.id == DISCIPLINE_INVALID_ID ||
                        it.teacher.id == TEACHER_INVALID_ID ||
                        it.group.id == GROUP_INVALID_ID ||
                        it.startTime === LESSON_INVALID_TIMESTAMP ||
                        it.endTime === LESSON_INVALID_TIMESTAMP
            },
            callback = {
                Main.lessonInteractor.add(it)
            }
        )
    }

    val editLesson = Handler { ctx ->
        ResponseGenerator.generate(ctx, Lesson::class.java, userGson,
            dataFormatWrong = {
                it.discipline.id == DISCIPLINE_INVALID_ID ||
                        it.office.id == DISCIPLINE_INVALID_ID ||
                        it.teacher.id == TEACHER_INVALID_ID ||
                        it.group.id == GROUP_INVALID_ID ||
                        it.startTime === LESSON_INVALID_TIMESTAMP ||
                        it.endTime === LESSON_INVALID_TIMESTAMP
            },
            callback = {
                Main.lessonInteractor.edit(it.copy(id = ctx.getParamId()))
            }
        )
    }

    val deleteLesson = Handler { ctx ->
        val errorMessage = Main.lessonInteractor.delete(ctx.getParamId())
        if (errorMessage != null) {
            ctx.showError(errorMessage)
        } else {
            ctx.redirect(Path.SUCCESS)
        }
    }

    private fun Context.getParamId(): Int {
        return pathParam(":lesson-id").toIntOrNull() ?: LESSON_INVALID_ID
    }
}