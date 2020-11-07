package cf.demidko

import org.springframework.jdbc.core.JdbcTemplate
import org.sqlite.SQLiteDataSource
import org.testng.annotations.Test
import java.sql.DriverManager.getConnection

class UtilsKtTest {

  @Test
  fun testSqlite() {
    val x =  JdbcTemplate(SQLiteDataSource()).apply {
      execute(" CREATE TABLE IF NOT EXISTS users (login TEXT NOT NULL, hash TEXT NOT NULL)")
    }

  }
}