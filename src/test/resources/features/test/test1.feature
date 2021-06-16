# language: ru
@data=$0002

@0002
Функциональность: Тестовый пример

  Предыстория: Получение токена

    * Авторизация с запросом json/auth.json на портале http://dev-keycloak.apps.d0-oscp.corp.dev.vtb/auth/realms/Portal/protocol/openid-connect/token
      | username | portal_admin_at |
      | password | portal_admin_at|
    * Сохранить access_token из json-тела последнего http-сообщения в переменную access_token

  Сценарий: Просмотр токена
    
     * вывести в консоль переменную access_token

 # Сценарий: Инсерт данных
 #   * вывести в консоль переменную param
 #   * вывести в консоль переменную param
 #   * Убедиться в истинности выражения param == customerNum