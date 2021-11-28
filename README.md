# CHIP-8 Emulator made with Java #

Very good emulator / interpreter. Currently runs everything made for CHIP-8 instruction set.
Lots of different features, great tool for learning CHIP-8 or making games for it.

![Emu](/misc/emul.png)

Quite cool feature is the fact that this emulator tries to remove some of the
typical stutter and flicker CHIP-8 has by fading pixels rather than turning them
straight off. The effect works great but has to be adjusted for each rom to get
the best possible result.

![Emulator](/misc/emulator.gif)

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

Generate JavaDoc with

```
mvn javadoc:javadoc
```