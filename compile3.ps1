Get-ChildItem -Path src -Filter *.java -Recurse | % { '"{0}"' -f ($_.FullName -replace "\\", "/") } | Out-File sources.txt -Encoding default
javac -d bin "@sources.txt"
java -cp bin app.RideShareEngine
