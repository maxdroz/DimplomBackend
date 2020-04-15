import java.io.File
import java.io.FileReader
import java.sql.DriverManager

class DB {
    companion object {
        val conn by lazy {
            val url = "jdbc:postgresql://192.168.0.25/testdb"
            val conn = DriverManager.getConnection(url, "postgres", "a")
            var script = ""
            FileReader(File("init.sql")).use {
                script = it.readText()
            }
            conn.createStatement().apply {
                executeUpdate(script)
            }
            conn
        }
    }
}
