### Injekt -- пример SQL иньекции
Состоит из HTTP наносервиса регистрации новых пользователей и приложенного для наглядности UI регистрации, совсем как на настоящем сайте.  

#### Как запустить бекенд?
1. Собираем исходный код из репозитория в исполняемый файл:  
`mvn clean package` 
1. Запускаем бекенд на локальном хосте:  
`java -jar target/injekt-1.0-SNAPSHOT.jar `

В появившейся консоли логируется происходящее на бекенде.  
Пример запроса на регистрацию нового пользователя, передаем логин и хеш пароля в md5:  
`localhost:8080/register?login=daniil&hash=96d0b7696590408b67e74d38b2e082c4`  
Запрос на перечисление всех зарегистрированных пользователей:  
`localhost:8080/list`

### 