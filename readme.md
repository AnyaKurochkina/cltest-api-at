updates - рабочая ветка с актуальными доработками фреймворка и текущей разработкой автотестов.
Сюда заливаются обновления из ветки vtb, а также новые тесты/доработки старых (в том числе через merge request).

Конфигурация VM Options для локального запуска через IntelliJ IDEA (на примере Smoke):

-ea -Dcucumber.options="--plugin io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm --tags @0002" -Denv=DEV -Denv=DEV

mvn test -Dcucumber.options="--plugin io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm --tags @0002" -Denv=IFT