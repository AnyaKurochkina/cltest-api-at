# language: ru
@0002
Функциональность: Тестовый пример

  Предыстория: Получение токена
    * Послать HTTP запрос json/test1.json в эндпоинт http://dev-keycloak.apps.d0-oscp.corp.dev.vtb/auth/realms/Portal/protocol/openid-connect/token
      | Content-Type | application/x-www-form-urlencoded |
    * Сохранить access_token из json-тела последнего http-сообщения в переменную access_token
    * вывести в консоль переменную access_token
  
  Сценарий: Получение токена
    
     * вывести в консоль переменную access_token

 # Сценарий: Инсерт данных
 #   * вывести в консоль переменную param
 #   * вывести в консоль переменную param
 #   * Убедиться в истинности выражения param == customerNum