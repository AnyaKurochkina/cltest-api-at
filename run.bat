@echo off

chcp 1251

set LOCAL_HOME=%cd%\

set BIN_PATH=bin\start.jar
set CP=libs\*
set MAIN_CLASS="ru.vtb.clp.runners.MainTestRunner"

set JAVA_OPTS=-Dlog4j.configuration=file:%LOCAL_HOME%\config\log4j.properties
set JAVA_OPTS=%JAVA_OPTS% -Dapplication.properties=%LOCAL_HOME%\config\application.properties
set JAVA_OPTS=%JAVA_OPTS% -Dcucumber.options="--tags @%1 features/"

java %JAVA_OPTS% -cp %CP%;%BIN_PATH% %MAIN_CLASS%