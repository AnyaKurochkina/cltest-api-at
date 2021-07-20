develop - рабочая ветка с актуальными доработками фреймворка и текущей разработкой автотестов.
Сюда заливаются обновления из ветки vtb, а также новые тесты/доработки старых (в том числе через merge request).
При старте работы сразу делайте свою ветку от ветки develop.

Для запуска в однопоточном режиме через кнопку run необходимо в edit configuration указать следующую строчку в vm options:
-ea -Denv=DEV
а так же выбрать исполняемый раннер (в котором должы быть прописаны теги запускаемых feature файлов)
вы можете так же указать теги непосредственно в строке запуска так:
-ea -Dcucumber.filter.tags=@put -Denv=DEV

Для запуска в параллельном режиме использовать слудующую команду:
mvn -Dtest=перечисляем названия ранеров через запятую -Denv=укзаываем среду
Пример:
mvn -Dtest=PostgresSQLRunnerTest,RunCucumber2Test,RunCucumberTest -Denv=DEV test

При запуске можно отфильтровать тесты внутри раннеров по дополнительному тегу изпользуя команду
-ea -D"extended.filter.tags=@000D"

Например:
mvn -Dtest=CreateBlocksTest,CreateProductsTest -Denv=DEV -Dextended.filter.tags=@smoke test