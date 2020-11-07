/**
 * Kotlin практически ничем особенным не отличается от C-подобных языков, кроме того что типы описываются как в паскале
 * val x: Type = ...
 *
 * По легенде, это приложение -- наносервис занимающийся регистрацией пользователей.
 * Он использует библиотеку spring-boot для работы с сервером и входящими HTTP запросами.
 */
package cf.demidko

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.sqlite.SQLiteDataSource


/**
 * Класс отвечает за пользователя, хранит логин и хеш от пароля.
 * Свойства классов в Kotlin можно описать сразу после имени. Это одновременно и есть конструктор.
 * Ключевое слово 'data' автоматически перегружает для класса оператор сравнения ==,
 * добавляет toString и прочие полезные мелочи.
 */
data class User(val login: String, val hash: String)


/**
 * Контроллер отвечает за обработку разных HTTP запросов к сервису, автоматический прием и возврат всего в JSON,
 * проставление кодов ответов и заголовков HTTP и прочие вещи в которые нам лень вникать,
 * ведь хотелось бы чтобы все работало из коробки.
 *
 * Чтобы класс стал контроллером и подцепился для обработки HTTP запросов, используются магические аннотации.
 */
@RestController
class Controller {

  /**
   * В свойстве хранится соединение с базой данных sqlite которая автоматически создастся при первом запуске приложения
   */
  private val sqlite = JdbcTemplate(SQLiteDataSource()).apply {
    execute(" CREATE TABLE IF NOT EXISTS users (login TEXT NOT NULL, hash TEXT NOT NULL)")
  }


  /**
   * HTTP запрос на регистрацию нового пользователя.
   * Благодаря @GetMapping spring-boot понимает какой именно запрос мы желаем обрабатывать этим методом,
   * а благодаря @RequestParam какие именно параметры должны быть у этого запроса.
   */
  @GetMapping("/register")
  fun registerNewUser(@RequestParam login: String, @RequestParam hash: String) = sqlite.execute(
    "INSERT INTO users(login, hash) VALUES ('$login', '$hash')"
  )


  /**
   * HTTP запрос на перечисление всех зарегистрированных пользователей в json
   */
  @GetMapping("/all-users")
  fun listAllUsers(): List<User> = sqlite.queryForList("SELECT * FROM users", User::class.java)
}


/**
 * Благодаря магической аннотации этот класс превратится в backend-приложение со встроенным HTTP сервером,
 * и сам сможет найти и подцепить контроллер (вот для чего контроллеру нужны были аннтоации @)
 */
@SpringBootApplication
open class Backend


/**
 * Точка входа запускает бекенд
 */
fun main() {
  runApplication<Backend>()
}




