develop - рабочая ветка с актуальными доработками фреймворка и текущей разработкой автотестов.

Для запуска через идею необходимо в edit configuration указать следующую строчку в vm options:
-ea -Denv=DEV -Dsecret=***
и выставить запуск по тегам, например regress

Пример для maven Например:
test -Denv=IFT -Dgroups=teamcity -Dsecret=***

В папке config можно создать application.properties со следующими настройками:
secret=(секрет)
env=(стенд)
testIt=(true/false интеграция с тестИт)
testItToken=(токен тестИт)
#ui settings
driver.path = seleniumDrivers/chromedriver99.0.4844.51.exe
webdriver.is.remote=false
#webdriver.remote.url=http://localhost:4444/wd/hub
webdriver.remote.url=http://d1-cloud-sel01.service.t1-cloud.ru:4444/wd/hub

Ключ -DtestItCreateAutotest=true мокает выполнение тестов (может быть полезен при создании и линковке автотестов с ручными тестами)