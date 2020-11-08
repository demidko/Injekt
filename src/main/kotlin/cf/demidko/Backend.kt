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

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.sql.DriverManager.getConnection


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
  fun registerNewUser(@RequestParam login: String, @RequestParam hash: String) {
    sqlite.execute("INSERT INTO users(login, hash) VALUES ('$login', '$hash')".also(::println))
  }


  /**
   * HTTP запрос на перечисление всех зарегистрированных пользователей в json
   */
  @GetMapping("/list")
  fun listAllUsers() = sequence {
    val it = sqlite.executeQuery("SELECT * FROM users")
    while (it.next()) yield(User(
      it.getString("login"),
      it.getString("hash")
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
  runApplication<Backend>(*args)
}




