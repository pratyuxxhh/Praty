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

<div align="center">
<img width="800" alt="praty basic commands" src="https://github.com/user-attachments/assets/3c472d67-63dd-47ef-9692-f9813a8ed680" />
</div>

It's now also a **Spring Boot project assistant** — an interactive terminal wizard that creates new Spring Boot projects, adds or removes dependencies from existing ones, and talks directly to [Spring Initializr](https://start.spring.io) to keep everything up to date.

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

<details>
<summary><b>Key pieces of the Spring layer</b></summary>

| Class | Role |
|---|---|
| `springx.commands.SetupCommand` | Launches the interactive project wizard |
| `springx.commands.AddCommand` | Adds dependencies to an existing project |
| `springx.commands.RemoveCommand` | Removes dependencies from an existing project |
| `springx.services.MetadataService` | Loads live metadata, with cached/mock fallback |
| `springx.network.HttpInitializrClient` | Talks to Spring Initializr |
| `springx.dependency.*` | Finds starters and edits the build file |
| `springx.wizard.*` | Drives the interactive setup screens |

</details>

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

Spins up a brand-new Spring Boot project from scratch:

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

<div align="center">
<img width="600" alt="spring setup wizard" src="https://github.com/user-attachments/assets/49a33c8f-5016-4b53-89d1-ff5d449fc1fb" />

<br/><br/>

<img width="400" alt="spring setup step 1" src="https://github.com/user-attachments/assets/bea664d7-1c13-4053-848a-d5363147e6ce" />
<img width="400" alt="spring setup step 2" src="https://github.com/user-attachments/assets/8bff9998-3d34-4f2d-9388-2dcbf00802ef" />

<br/><br/>

<img width="300" alt="project ready" src="https://github.com/user-attachments/assets/35378910-77ec-4069-a13d-e4a68fd6fe97" />

<i>Your project, ready in seconds.</i>
</div>

### `praty spring add`

Forgot to add a dependency? Run this inside an existing Spring Boot project — it browses the same dependency catalog and adds selected starters straight into your Maven or Gradle build.

<div align="center">
<img width="600" alt="spring add dependencies" src="https://github.com/user-attachments/assets/ab6fcefd-5723-4db6-8c91-93ff610435b4" />
</div>

### `praty spring remove`

Don't want `lombok` in your project anymore? The reverse of `add` — opens the dependency browser, lets you pick starters to remove, and cleans up the build file.

<div align="center">
<img width="600" alt="spring remove dependencies" src="https://github.com/user-attachments/assets/b74691f0-f2f0-432f-85f3-be44ea7fdc44" />
</div>

### `praty spring deps`

Shows a summary of Spring dependencies recently used.

<div align="center">
<img width="450" alt="spring deps summary" src="https://github.com/user-attachments/assets/bbee4f15-540f-490a-9135-fe42fc820ac8" />
</div>

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
