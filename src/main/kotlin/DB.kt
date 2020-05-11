import java.io.File
import java.io.FileReader
import java.sql.DriverManager

private const val DATABASE_NAME = "testdb"
private const val DATABASE_USER = "postgres"
private const val DATABASE_PASSWORD = "a"
private const val DATABASE_URL = "jdbc:postgresql://192.168.0.25/"

class DB {
    companion object {
        val conn by lazy {
            var conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)
            conn.createStatement().apply {
                val ans = executeQuery("SELECT datname FROM pg_catalog.pg_database WHERE datname = '$DATABASE_NAME';")
                if(!ans.next()) {
                    conn.createStatement().apply {
                        executeUpdate("CREATE DATABASE $DATABASE_NAME;")
                    }
                }
            }
            conn.close()
            conn = DriverManager.getConnection(DATABASE_URL + DATABASE_NAME, DATABASE_USER, DATABASE_PASSWORD)
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
