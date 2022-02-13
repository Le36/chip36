# CHIP-36 #

### Cross-platform CHIP-8 Emulator made with Java ###

Chip-8 interpreter and debugger. Currently runs everything made for CHIP-8, S-CHIP or XO-CHIP instruction set. Lots of
different features, great tool for learning CHIP-8 or making ROMs for it.

![Emu](/misc/emul.png)

There are two different modes for the emulator. With the extended mode you have all the registers visible to you all the time.
With the normal mode you can focus on running the ROM without all the noise, showing you only the ROM.

![Emulator](/misc/emulator.gif)

Quite cool feature is the fact that this emulator tries to remove some typical stutter and flicker CHIP-8 has by
fading pixels rather than turning them straight off. The effect works great but has to be adjusted for each rom to get
the best possible result. It can also be used to create cool trailing effects for sprites. 
Effect works even in XO-CHIP 4 color ROMs, but often in these ROMs it's not required and may affect performance.

![Fade](/misc/fade.gif)

There is a lot of customization available. Glow and blur effects for the game, all the colors are adjustable as well.
With quirks, you can get any ROM running on this emulator, legacy CHIP-8 or modern XO-CHIP ROM, everything will work.
Keyboard is rebindable, so you can truly make them ROMs feel good no matter which platform you are running this emulator.

![Options](/misc/options.png)

ROMs can be played from terminal and combined for example with [cool-retro-term](https://github.com/Swordfish90/cool-retro-term) 
you can play ROMs like it's 1970. With XO-CHIP ROMs only the default plane will be printed to terminal.

![Console](/misc/console.gif)

Loaded ROM can be disassembled during runtime. It is possible to follow program counter, or view the emulators RAM by hand.
It's possible to see sprites easily with the graphical view feature.

![Disassembler](/misc/disassembler.png)

There is separate sprite viewer that shows how the sprites are drawn. Sprites can also be extracted from the ROM.
Sprite data can then be easily copied and used in Octo assembler.

![Sprites](/misc/sprites.png)


## How to run ##

Requires Java 11 or newer to run.

### Start

Clone project and use this command when in root folder to launch the program

Linux ```mvn compile exec:java -Dexec.mainClass=com.chip8.Main```

Windows ```mvn compile exec:java -D"exec.mainClass"="com.chip8.Main"```

or you can build with

```
mvn package
```

or get the prebuilt .jar file from [releases.](https://github.com/Le36/chip36/releases/tag/v1.0)

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