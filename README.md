<<<<<<< HEAD
# praty — Minimal CLI (Java 21 + Maven)

This repository contains a minimal, extensible CLI called `praty` implemented in Java 21 and Maven.

Project layout

```
praty
├── pom.xml
└── src/main/java/praty
    ├── command
    │   ├── Command.java
    │   ├── CommandRegistry.java
    │   └── Parser.java
    ├── modules
    │   ├── awake
    │   │   └── AwakeCommand.java
    │   └── sleep
    │       └── SleepCommand.java
    └── Main.java
```

Build

Run the following from the project root to compile and package the JAR:

```powershell
mvn package
```

The executable JAR will be created at `target/praty-0.1.0.jar`.

Windows wrapper

Create a folder (example: `C:\tools\praty`) and copy these two files into it:

- `target/praty-0.1.0.jar` (rename to `praty-0.1.0.jar` or edit the batch script)
- `praty.bat` (from `scripts/praty.bat`)

`praty.bat` content:

```bat
@echo off
SET SCRIPT_DIR=%~dp0
java -jar "%SCRIPT_DIR%praty-0.1.0.jar" %*
```

Add the folder to your PATH (PowerShell example):

```powershell
setx PATH "%PATH%;C:\tools\praty"
```

After opening a new terminal you can run:

```powershell
praty awake
praty sleep
```
=======
# praty
>>>>>>> 09dd0a8c35d0b78d2f0da052581b7f812ac5ef3e
