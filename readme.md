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

Ключ -DtestItCreateAutotest=true мокает выполнение тестов (может быть полезен при создании и линковке автотестов с ручными тестами)