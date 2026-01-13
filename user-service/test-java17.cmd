@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
echo Testing Maven with Java 17...
mvn -v
echo.
echo Compiling user-service...
mvn clean compile
