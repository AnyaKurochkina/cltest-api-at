# autotests

1. Проверить / настроить файл конфигурации application.properties.  
    - host - адрес хоста 
    - dbUrl - url для коннекта к БД  
    - dbUser - логин к БД  
    - dbPassword - пароль к БД  
    
2. Запустить автотесты:  
   mvn clean test

3. Сгенерировать отчет Allure:  
   mvn allure:serve