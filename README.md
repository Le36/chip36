# CHIP8 Emulator made with Java #

Very good emulator

## How to run ##

### Start

Use this command when in root folder to launch the program

```
mvn compile exec:java -Dexec.mainClass=com.chip8.Main
```

or build

```
mvn package
```

### Testing

Execute all tests

```
mvn test
```

Jacoco code coverage report

```
mvn test jacoco:report
```

Checkstyle report

```
mvn jxr:jxr checkstyle:checkstyle
```