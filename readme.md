updates - рабочая ветка с актуальными доработками фреймворка и текущей разработкой автотестов.
Сюда заливаются обновления из ветки vtb, а также новые тесты/доработки старых (в том числе через merge request).

Конфигурация VM Options для локального запуска через IntelliJ IDEA (на примере Smoke):
-ea -Dcucumber.options="--tags @<тэг>" -Denv=<среда> -Dwave=<волна>


mvn -Drelease_url="<адрес релизного нексуса>" -Dsnapshot_url="<адрес снапшотного нексуса>" install -Dcucumber.options="--tags @<тег>" -s src/settings/settings.xml -Denv=<тут указываем стенд>
