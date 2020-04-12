package lesson

import com.google.gson.*
import com.google.gson.annotations.Expose
import discipline.DISCIPLINE_INVALID_ID
import discipline.Discipline
import io.javalin.http.Handler
import office.Office
import teacher.TEACHER_INVALID_ID
import teacher.Teacher
import utils.ResponseGenerator
import java.sql.Timestamp

object LessonController {
    val gson by lazy {
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
        builder.registerTypeAdapter(Timestamp::class.java, timestampDeserializer)
        builder.registerTypeAdapter(Timestamp::class.java, timestampSerializer)
        builder.registerTypeAdapter(Discipline::class.java, disciplineDeserializer)
        builder.registerTypeAdapter(Discipline::class.java, disciplineSerializer)
        builder.registerTypeAdapter(Office::class.java, officeSerializer)
        builder.registerTypeAdapter(Office::class.java, officeDeserializer)
        builder.registerTypeAdapter(Teacher::class.java, teacherDeserializer)
        builder.create()
    }

    val fetchAllLessons = Handler { ctx ->
        val from: Long? = ctx.req.getParameter("from")?.toLong()
        val to: Long? = ctx.req.getParameter("to")?.toLong()
        ctx.html(gson.toJson(hashMapOf("lessons" to Main.lessonInteractor.getFiltered(from, to))))
    }

    val addLesson = Handler { ctx ->
        ResponseGenerator.generate(ctx, Lesson::class.java, gson,
            dataFormatWrong = {
                it.discipline.id == DISCIPLINE_INVALID_ID ||
                        it.office.id == DISCIPLINE_INVALID_ID ||
                        it.teacher.id == TEACHER_INVALID_ID ||
                        it.startTime === LESSON_INVALID_TIMESTAMP ||
                        it.endTime === LESSON_INVALID_TIMESTAMP
            },
            callback = {
                Main.lessonInteractor.add(it)
            }
        )
    }

    val editLesson = Handler { ctx ->
        ResponseGenerator.generate(ctx, Lesson::class.java, gson,
            dataFormatWrong = {
                it.id == LESSON_INVALID_ID ||
                        it.discipline.id == DISCIPLINE_INVALID_ID ||
                        it.office.id == DISCIPLINE_INVALID_ID ||
                        it.teacher.id == TEACHER_INVALID_ID ||
                        it.startTime === LESSON_INVALID_TIMESTAMP ||
                        it.endTime === LESSON_INVALID_TIMESTAMP
            },
            callback = {
                Main.lessonInteractor.edit(it)
            }
        )
    }

    val deleteLesson = Handler { ctx ->
        ResponseGenerator.generate(ctx, Lesson::class.java, gson,
            dataFormatWrong = {
                it.id == LESSON_INVALID_ID
            },
            callback = {
                Main.lessonInteractor.delete(it.id)
            }
        )
    }
}