@echo off
echo Cleaning up old files...
if exist src\models\Rider.java del src\models\Rider.java
if exist src\strategies\MatchingStrategy.java del src\strategies\MatchingStrategy.java
if exist src\strategies\NearestDriverMatchingStrategy.java del src\strategies\NearestDriverMatchingStrategy.java
if exist src\strategies\PricingStrategy.java del src\strategies\PricingStrategy.java
echo Compiling project...
if not exist bin mkdir bin
dir /s /B src\*.java > sources.txt
powershell -Command "(gc sources.txt) -replace '^', '\"\"\"' -replace '$', '\"\"\"' | Out-File -encoding ASCII sources.txt"
javac -d bin @sources.txt
del sources.txt
echo Running RideShareEngine...
java -cp bin app.RideShareEngine
pause
