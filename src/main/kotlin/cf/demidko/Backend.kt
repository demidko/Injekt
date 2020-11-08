/**
 * Kotlin ничем особенным не отличается от C-подобных языков, кроме того что типы описываются как в паскале
 * val x: Type = ...expression
 * fun x(): Type { return ...expression }
 *
 * Типы можно опускать
 * val x = ...expression
 * fun x() = ...expression
 *
 */
package cf.demidko

import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.sql.DriverManager.getConnection
import java.sql.Statement


/**
 * Класс представляет пользователя, хранит логин и хеш от пароля.
 * Свойства класса в Kotlin можно писать сразу после имени, описание одновременно является конструктором.
 */
class User(val login: String, val hash: String)


/**
 * Контроллер отвечает за обработку входящих HTTP запросов и конвертацию их в параметры,
 * за проставление кодов состояния, заголовков, конвертацию в JSON исходящих HTTP ответов,
 * и прочие вещи в которые нам лень вникать, ведь хотелось бы чтобы все работало из коробки.
 *
 * Чтобы класс стал контроллером и подцепился для обработки HTTP запросов, используются "магические" аннотации.
 */
@RestController
class Controller {

  private val log = getLogger(Controller::class.java)

  /**
   * Соединение с базой данных
   */
  private val sqlite = getConnection("jdbc:sqlite:sqlite.db").createStatement().apply {
    execute(" CREATE TABLE IF NOT EXISTS users (login TEXT NOT NULL, hash TEXT NOT NULL)")
  }


  /**
   * HTTP запрос на регистрацию нового пользователя.
   * Благодаря @GetMapping spring-boot понимает какой именно запрос мы желаем обрабатывать этим методом,
   * а благодаря @RequestParam какие именно параметры должны быть у этого запроса.
   */
  @GetMapping("/register")
  fun tryRegisterNewUser(@RequestParam login: String, @RequestParam hash: String) {
    val findCurrentUserSql = "SELECT * FROM users WHERE login = '$login'".also(log::info)
    if (sqlite.executeQueryForUsers(findCurrentUserSql).any()) {
      throw ResponseStatusException(CONFLICT, "User '$login' already exists")
    }
    val insertCurrentUserSql = "INSERT INTO users(login, hash) VALUES ('$login', '$hash')".also(log::info)
    sqlite.execute(insertCurrentUserSql)
  }


  /**
   * HTTP запрос на перечисление всех зарегистрированных пользователей в json
   */
  @GetMapping("/list")
  fun listAllUsers() = sqlite.executeQueryForUsers("SELECT * FROM users")


  /**
   * Метод для извлечения пользователей по запросу
   */
  private fun Statement.executeQueryForUsers(sql: String) = sequence {
    // Выполняем запрос и получаем найденные записи
    val resultSet = executeQuery(sql)
    // Итерируемся по найденным записям и данные в список пользователей
    while (resultSet.next()) yield(User(
      resultSet.getString("login"),
      resultSet.getString("hash")
    ))
  }
}


/**
 * Благодаря очередной "магической" аннотации этот класс превратится в backend-приложение и сможет подцепить контроллер.
 * Модификатор open означает что от этого класса разрешено наследование и нужен для работы аннотации.
 */
@SpringBootApplication
open class Backend


/**
 * Точка входа запускает бекенд
 */
fun main(args: Array<String>) {
  runApplication<Backend>(*args, "--server.error.include-message=always")
}




