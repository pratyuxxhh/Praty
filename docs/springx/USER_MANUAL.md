# PRATY Spring commands — user manual

Commands that are **implemented today** (Phases 1–5). Planned commands are listed only so you know they are not available yet.

---

## Prerequisites

- **Java 21+** on your PATH (`java -version`)
- **Internet** the first time you run `praty spring setup` or `praty spring add` (metadata is cached under `~/.praty/springx/cache/`)
- Optional: install PRATY with [`build.md`](../../build.md) so `praty` works from any folder

---

## Install / refresh on Windows

Yes — you can use [`build.md`](../../build.md) to set up PRATY on Windows.

From the project root (`programs\PRATY`):

```powershell
mvn clean package
Copy-Item target\praty-0.1.0.jar target\praty.jar -Force
New-Item -ItemType Directory -Force -Path C:\praty | Out-Null
Copy-Item target\praty.jar C:\praty\praty.jar -Force
Copy-Item scripts\praty.bat C:\praty\praty.bat -Force
```

Add `C:\praty` to your user PATH (once), then **open a new terminal**:

```powershell
setx PATH "$env:PATH;C:\praty"
```

Check:

```powershell
praty spring setup --help
```

If `praty` is not found, run with the full path: `C:\praty\praty.bat spring setup`.

---

## Keyboard controls (interactive screens)

| Key | Action |
|-----|--------|
| `↑` / `↓` | Move highlight |
| `Enter` | Confirm / finish selection |
| `Space` | Toggle dependency on/off (works in search mode too) |
| `/` | Start searching dependencies |
| `Esc` | Exit search, or cancel a step |
| `Ctrl+C` | Abort |

---

## Live commands

### `praty spring setup`

Creates a new Spring Boot project with an interactive wizard (Initializr over HTTP — **no** Spring CLI required).

**Important:** run this from a folder **outside** the PRATY repository (for example `Desktop\programs`), so the new project does not land inside this CLI repo.

```powershell
cd C:\Users\ishuk\OneDrive\Desktop\programs
praty spring setup
```

Wizard steps:

1. Project name  
2. Package name  
3. Java version  
4. Build tool (Maven / Gradle)  
5. Packaging (Jar / War)  
6. Spring Boot version (from Initializr)  
7. Language  
8. Dependencies (searchable multi-select)  
9. Summary + confirm  

Then PRATY downloads the ZIP and extracts it into a folder named after your project (e.g. `my-blog\`).

Help:

```powershell
praty spring setup --help
```

---

### `praty spring add`

Adds dependencies to the **current** Spring Boot project by editing `pom.xml` or `build.gradle`.

Run from inside your Spring project (or a subdirectory of it):

```powershell
cd C:\Users\ishuk\OneDrive\Desktop\programs\my-blog
praty spring add
```

What happens:

1. Detects Maven or Gradle  
2. Loads the Initializr dependency catalog (live or cache)  
3. Opens a searchable browser (favorites, recent, recommendations first)  
4. Shows a preview and asks to confirm  
5. Writes starter coordinates into the build file  

Example: select **Spring Web** → adds `spring-boot-starter-web`.

---

### `praty spring remove`

Removes Initializr-style starters that PRATY can detect in the build file.

```powershell
cd C:\Users\ishuk\OneDrive\Desktop\programs\my-blog
praty spring remove
```

Select deps with Space, confirm, and they are stripped from `pom.xml` / `build.gradle`.

Custom / non-starter dependencies may not appear — edit those by hand if needed.

---

### `praty spring deps`

Shows how often you have used dependency ids (history under `~/.praty\dependency-usage.json`).

```powershell
praty spring deps
```

Useful after several `setup` / `add` runs. This is **not** a full catalog browser yet.

---

## Config and cache (reference)

| Path | Purpose |
|------|---------|
| `%USERPROFILE%\.praty\springx\config.json` | Theme, defaults, favorites, recent projects, usage |
| `%USERPROFILE%\.praty\springx\cache\initializr-metadata.json` | Offline Spring Initializr metadata |
| `%USERPROFILE%\.praty\dependency-usage.json` | Dependency usage counts for `spring deps` |

---

## Typical workflows

### New project

```powershell
cd C:\Users\ishuk\OneDrive\Desktop\programs
praty spring setup
cd .\my-blog
```

### Add Redis / Web later

```powershell
cd C:\Users\ishuk\OneDrive\Desktop\programs\my-blog
praty spring add
# / then type redis or web, Space to select, Enter, confirm
```

### Remove a starter

```powershell
praty spring remove
```

### Rebuild PRATY after code changes

Follow [`build.md`](../../build.md) (or the PowerShell steps above) so `C:\praty\praty.jar` is updated.

---

## Other PRATY commands (unchanged)

These still work alongside Spring:

| Command | Purpose |
|---------|---------|
| `praty awake` | Wake / power helper |
| `praty sleep` | Sleep |
| `praty shutdown` | Shutdown |
| `praty restart` | Restart |
| `praty file cp` / `mv` / `-d` / `unzip` | File helpers |
| `praty app -add` / `-o` / `-ls` / `-rm` | App launcher |
| `praty man` | Manual |
| `praty check update` | Update check |

---

## Not implemented yet

These appear in the architecture docs but are **not** wired:

- `spring new`, `spring update`, `spring doctor`, `spring generate …`
- `spring template …`, `spring plugins`, `spring config`, `spring cache clean`, etc.

If you run them you will get an unknown-command error.

---

## Troubleshooting

| Problem | What to do |
|---------|------------|
| `Unknown command: spring add` | Rebuild and reinstall (`build.md`); old JAR lacks Phase 5 |
| Refuses to create inside PRATY repo | Run `setup` from a parent folder (e.g. `Desktop\programs`) |
| No project found on `add` / `remove` | `cd` into a folder with `pom.xml` or `build.gradle` |
| Metadata download failed | Check internet; later runs can use the cache |
| Keys feel broken in Windows Terminal | Prefer Windows Terminal / modern console; avoid pipes that strip TTY |
| `praty` not found | Add `C:\praty` to PATH and open a **new** shell |
