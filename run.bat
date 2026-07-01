@echo off
echo Cleaning up old files...
if exist src\models\Rider.java del src\models\Rider.java
echo Compiling project...
if not exist bin mkdir bin
dir /s /B src\*.java > sources.txt
javac -d bin @sources.txt
del sources.txt
echo Running RideShareEngine...
java -cp bin app.RideShareEngine
pause
