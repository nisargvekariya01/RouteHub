# Step 1: Collect all Java source files under 'src' (recursively)
# Format their paths with forward slashes and wrap in quotes
# Save the list into sources.txt
Get-ChildItem -Path src -Filter *.java -Recurse |
    % { '"{0}"' -f ($_.FullName -replace "\\", "/") } |
    Out-File sources.txt -Encoding default

# Step 2: Compile all Java files listed in sources.txt
# Place the compiled .class files into the 'bin' directory
javac -d bin "@sources.txt"

# Step 3: Run the program
# Use 'bin' as the classpath and execute the main class 'app.RideShareEngine'
java -cp bin app.RideShareEngine
