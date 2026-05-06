@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%PATH%;%JAVA_HOME%\bin
cd /d C:\Users\ADMIN\pharmacy-management-system\backend\pharmacy-api
call .\mvnw.cmd -DskipTests clean spring-boot:run
