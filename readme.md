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

Ключ -DtestItCreateAutotest=true мокает выполнение тестов (может быть полезен при создании и линковке автотестов с ручными тестами)

####Для синхронизации битбакет с gitlab выполнить команду в папке с проектом:
`py -m sync_git_repos`

Перед этим:
1. Установить модуль `pip install sync-git-repos`
2. Добавить публичный ssh ключ в gitlab. Если нужно создать новый:

`eval 'ssh-agent -s'`

`ssh-keygen -t rsa -b 4096 -C "git"`

`ssh-add ~/.ssh/id_rsa`

Публичный ключ в`~/.ssh/id_rsa.pub` 
