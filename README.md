# praty

`praty` is a Java 21 + Maven CLI that grew from a few basic system commands into a Spring Boot helper tool with an interactive terminal workflow.

## What Changed

The original CLI was a small command router for things like wake, sleep, file actions, and app shortcuts. The newer architecture adds a Spring-focused subsystem called `springx`, which handles project setup and dependency changes through an interactive JLine wizard, Spring Initializr metadata, local config/cache storage, and build-file editing for Maven or Gradle projects.

In simple terms, the app now looks like this:

`Main` -> `Parser` -> `CommandRegistry` -> module command -> Springx services, wizard, network, validation, and dependency editors.

## Architecture

The codebase is organized into two broad parts.

The first part is the command layer under `src/main/java/praty`, where the CLI parses user input and routes it to commands such as `awake`, `sleep`, `file`, `app`, `check`, `get`, `cd`, `spring`, and `man`.

The second part is the Spring layer under `src/main/java/praty/springx`, which is responsible for:

- interactive project creation
- browsing available Spring Boot versions and dependencies
- adding and removing dependencies in existing projects
- reading and caching Spring Initializr metadata
- validating project locations and names
- editing Maven or Gradle build files
- rendering terminal UI and progress states
- storing local preferences and recent project data

The Spring workflow is built around a few clear pieces:

- `springx.commands.SetupCommand` launches the interactive project wizard.
- `springx.commands.AddCommand` adds dependencies to an existing Spring Boot project.
- `springx.commands.RemoveCommand` removes dependencies from an existing Spring Boot project.
- `springx.services.MetadataService` loads live Spring Initializr metadata and falls back to cached or mock metadata when needed.
- `springx.network.HttpInitializrClient` talks to Spring Initializr.
- `springx.dependency.*` finds the right starter dependencies and edits the build file.
- `springx.wizard.*` drives the interactive setup screens.

## Commands

`awake`, `sleep`, `shutdown`, `restart`, `check update`, `check updates`, `get update`, `get updates`, `file cp`, `file mv`, `file -d`, `file unzip`, `app -add`, `app -o`, `app -open`, `app -ls`, `app -rm`, `app -r`, `cd ~`, `spring setup`, `spring add`, `spring remove`, `spring deps`, `man`

## Spring Version And Workflow

The new Spring side of `praty` is designed for Spring Boot project generation and dependency management. It does not just print a template; it asks questions, fetches metadata, and then creates or updates a real project based on those answers.

When you run `praty spring setup`, the tool:

1. Loads Spring Initializr metadata.
2. Opens an interactive JLine wizard in the terminal.
3. Lets you choose project name, group, artifact, build tool, language, packaging, Java version, Spring Boot version, and dependencies.
4. Validates the target folder so it does not accidentally create a project in the wrong place.
5. Calls Spring Initializr to download the starter project.
6. Extracts and configures the project.
7. Saves local usage data and recent project settings.

`praty spring add` is for an existing Spring Boot project. It loads the same metadata and dependency catalog, then lets you browse available starters and add selected ones into the current Maven or Gradle build.

`praty spring remove` does the reverse. It opens the dependency browser, lets you pick starters to remove, and updates the build file cleanly.

`praty spring deps` shows the Spring dependency usage summary.

The Spring Boot version is handled dynamically from metadata, with cached/offline fallback support. That means the CLI can follow available Spring Boot releases instead of being stuck on one hardcoded template. The default project setup in the codebase still starts from Java 21, and the generated project values are normalized so the artifact name, target directory, and package naming stay consistent.

### Setup

Run `praty spring setup` and follow the wizard.

[Insert screenshot here]

[Insert screenshot here]

### Add

Run `praty spring add` inside an existing Spring Boot project.

[Insert screenshot here]

[Insert screenshot here]

### Remove

Run `praty spring remove` inside an existing Spring Boot project.

[Insert screenshot here]

[Insert screenshot here]

## Build

Run the following from the project root to compile and package the JAR:

```powershell
mvn package
```

The executable JAR will be created at `target/praty-0.1.0.jar`.

## Windows Setup

To use `praty` on Windows, create a folder such as `C:\tools\praty` and copy these two files into it:

- `target/praty-0.1.0.jar`
- `scripts/praty.bat`

The batch file can be as simple as this:

```bat
@echo off
SET SCRIPT_DIR=%~dp0
java -jar "%SCRIPT_DIR%praty-0.1.0.jar" %*
```

After that, add the folder to your PATH. One easy PowerShell example is:

```powershell
setx PATH "%PATH%;C:\tools\praty"
```

Open a new terminal window and run the commands from anywhere:

```powershell
praty awake
praty sleep
praty spring setup
```

## How I Created It

In a very simple way, I built the project like this:

1. I created the Java project and built it with Maven, which produced a JAR file.
2. I made a small batch file so Windows could launch that JAR with one command.
3. I put both files in a folder on the C drive.
4. I wrote the batch script to call `java -jar` on the JAR file.
5. I added that folder to PATH so the command could be used from any terminal.

That is why `praty` feels like a normal command on Windows even though the actual app is a Java program running from a JAR.
