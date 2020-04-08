import org.eclipse.jetty.util.log.Slf4jLog
import spark.kotlin.get

fun main() {
    get("/"){
        return@get "Hello world"
    }
}