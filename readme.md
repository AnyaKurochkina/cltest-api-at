Чтобы подтянуть зависимости необходимо скопировать файл settings.xml из корня проекта
в свою папку .m2 которая находится по пути C:/users/user/.m2
после чего исполнить команду mvn clean (для установки cassandra)

develop - рабочая ветка с актуальными доработками фреймворка и текущей разработкой автотестов.

Для запуска через идею необходимо в edit configuration указать следующую строчку в vm options:
`-ea -Denv=DEV -Dsecret=***`
и выставить запуск по тегам, например regress

Пример для maven Например:
`test -Denv=IFT -Dgroups=teamcity -Dsecret=***`

В папке config можно создать application.properties со следующими настройками:
secret=(секрет)
env=(стенд)
testIt=(true/false интеграция с тестИт)
testItToken=(токен тестИт)
webdriver.is.remote=true/false


#ui settings
webdriver.path = seleniumDrivers/chromedriver99.0.4844.51.exe
#webdriver.remote.url=http://localhost:4444/wd/hub
webdriver.remote.url=http://d1-cloud-sel01.service.t1-cloud.ru:4444/wd/hub
Для просмотра тестов на selenoid http://10.13.240.91:8080/#/

Ключ -DtestItCreateAutotest=true мокает выполнение тестов (может быть полезен при создании и линковке автотестов с ручными тестами)

Почта для автотестов - autotest240124@gmail.com password 123Ion123
у этой почты есть права суперадмин на проде креды autotest240124@gmail.com 123!On123