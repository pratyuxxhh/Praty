<div align="center">

# ⚡ praty

**A Java CLI that grew from simple system shortcuts into a full Spring Boot project assistant.**

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-blue?style=flat-square&logo=apachemaven&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Initializr-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Windows-lightgrey?style=flat-square&logo=windows&logoColor=white)

</div>

---

## ✨ What is praty?

`praty` started as a tiny command router — wake, sleep, open an app, copy a file.

It's now also a **Spring Boot project assistant**: an interactive terminal wizard that creates new Spring Boot projects, adds or removes dependencies from existing ones, and talks directly to [Spring Initializr](https://start.spring.io) to keep everything up to date.

```
Main → Parser → CommandRegistry → module command → Springx (wizard, network, validation, dependency editors)
```

---

## 🧩 Architecture at a Glance

The codebase splits cleanly into two halves:

| Layer | Location | Responsibility |
|---|---|---|
| 🖥️ **Command Layer** | `src/main/java/praty` | Parses input and routes to commands like `awake`, `file`, `app`, `cd`, `spring` |
| 🌱 **Spring Layer** | `src/main/java/praty/springx` | Project creation, dependency management, Initializr metadata, build-file editing, terminal UI |

### Key Pieces of the Spring Layer

| Class | Role |
|---|---|
| `springx.commands.SetupCommand` | Launches the interactive project wizard |
| `springx.commands.AddCommand` | Adds dependencies to an existing project |
| `springx.commands.RemoveCommand` | Removes dependencies from an existing project |
| `springx.services.MetadataService` | Loads live metadata, with cached/mock fallback |
| `springx.network.HttpInitializrClient` | Talks to Spring Initializr |
| `springx.dependency.*` | Finds starters and edits the build file |
| `springx.wizard.*` | Drives the interactive setup screens |

---

## 📖 Commands

<table>
<tr><td valign="top">

**⚙️ System**
```
awake
sleep
shutdown
restart
```

</td><td valign="top">

**🔍 Updates**
```
check update(s)
get update(s)
```

</td><td valign="top">

**📁 Files & Apps**
```
file cp / mv / -d / unzip
app -add / -o / -open
app -ls / -rm / -r
cd ~
```

</td><td valign="top">

**🌱 Spring**
```
spring setup
spring add
spring remove
spring deps
```

</td></tr>
</table>

Need a refresher? Run:
```powershell
praty man
```

---

## 🌱 Spring Workflow

`praty` doesn't just print a template — it asks questions, fetches real metadata, and generates or updates an actual project.

### `praty spring setup`

```
 1. Load Spring Initializr metadata
 2. Open the interactive JLine wizard
 3. Choose name, group, artifact, build tool, language,
    packaging, Java version, Spring Boot version, dependencies
 4. Validate the target folder
 5. Call Spring Initializr and download the starter project
 6. Extract and configure the project
 7. Save local usage data and recent project settings
```

📸 *[Insert screenshot here]*
📸 *[Insert screenshot here]*

### `praty spring add`

Run inside an existing Spring Boot project. Browses the same dependency catalog and adds selected starters straight into your Maven or Gradle build.

📸 *[Insert screenshot here]*
📸 *[Insert screenshot here]*

### `praty spring remove`

The reverse of `add` — opens the dependency browser, lets you pick starters to remove, and cleans up the build file.

📸 *[Insert screenshot here]*
📸 *[Insert screenshot here]*

### `praty spring deps`

Shows a summary of Spring dependencies currently in use.

> 💡 The Spring Boot version is resolved **dynamically** from live metadata (with offline fallback), so `praty` follows current releases instead of being frozen to one template. New projects still default to **Java 21**, and generated values (artifact name, directory, package name) are normalized for consistency.

---

## 🔌 Under the Hood

| Library / API | Purpose |
|---|---|
| `org.jline:jline` | Interactive terminal wizard and prompts |
| `com.fasterxml.jackson.core:jackson-databind` | Parses Spring Initializr metadata JSON |
| `java.net.http.HttpClient` (Java 21) | Sends requests to Spring Initializr |

**Talks directly to `https://start.spring.io`:**

| Request | Purpose |
|---|---|
| `GET /` (`Accept: application/json`) | Loads Initializr metadata for the wizard |
| `POST /starter.zip` (`application/x-www-form-urlencoded`) | Downloads the generated project |

The form includes build tool, language, Spring Boot version, group, artifact, package name, Java version, and dependency IDs.

---

## 🛠️ Build from Source

```powershell
mvn package
```

The runnable JAR lands at:
```
target/praty-0.1.0.jar
```

---

## 🪟 Windows Setup

**1. Create a folder** — e.g. `C:\tools\praty` — and copy in:
- `target/praty-0.1.0.jar`
- `scripts/praty.bat`

**2. Keep the batch file simple:**
```bat
@echo off
SET SCRIPT_DIR=%~dp0
java -jar "%SCRIPT_DIR%praty-0.1.0.jar" %*
```

**3. Add the folder to your PATH:**
```powershell
setx PATH "%PATH%;C:\tools\praty"
```

**4. Open a new terminal and run it from anywhere:**
```powershell
praty awake
praty sleep
praty spring setup
```

---

## 🧱 How It Was Built

1. Built the Java project with Maven → produced a JAR
2. Wrote a small `.bat` launcher for Windows
3. Placed both files in a folder on the C drive
4. Had the batch script call `java -jar` on the JAR
5. Added that folder to PATH

That's the whole trick — `praty` feels like a native Windows command, even though it's really a Java program launched from a JAR.

---

<div align="center">

Made for a smoother terminal workflow — one Spring Boot project at a time. 🌱

</div>
