# A phone number to word parser
## Building
```bash
./gradlew build
```
## Running
Due to an existing gradle issue: https://issues.gradle.org/browse/GRADLE-3292 we have to manually run the resulting classes:
```bash
java -cp build/classes/java/main/ co.petrocco.DigitsToWords [-d dictionary] [filename(s)]
```
The program accepts a dictionary via -d, and input files via command line args
If no input files are passed in, it'll wait for input via stdin. A blank line terminates input.
