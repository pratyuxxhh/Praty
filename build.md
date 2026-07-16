# Build and install PRATY (Windows)

Build the shaded JAR, rename it to a stable name, and install under `C:\praty` so you can run `praty` from any folder.

## Requirements

- Java 21+ (`java -version`)
- Maven 3.9+ (`mvn -version`)

## Steps (PowerShell)

Run from the PRATY project root:

```powershell
# 1. Build
mvn clean package

# 2. Stable name (matches scripts\praty.bat)
Copy-Item target\praty-0.1.0.jar target\praty.jar -Force
New-Item -ItemType Directory -Force -Path C:\praty | Out-Null
Copy-Item target\praty.jar C:\praty\praty.jar -Force
Copy-Item scripts\praty.bat C:\praty\praty.bat -Force
```

### Add to PATH (once)

```powershell
setx PATH "$env:PATH;C:\praty"
```

Close and reopen the terminal, then:

```powershell
praty spring setup --help
```

## Notes

- If Maven produces a different version (for example `praty-0.2.0.jar`), copy that file to `C:\praty\praty.jar` instead — `praty.bat` always runs `praty.jar`.
- `mvn package` uses the shade plugin; `target\praty-0.1.0.jar` already includes dependencies (JLine, Jackson).
- Use any install folder you like; update PATH and the copy destination accordingly.
- After pulling new Spring features, run this guide again so `C:\praty` has a fresh JAR.

## Git Bash alternative

If you prefer Bash-style commands:

```bash
mvn clean package
cp target/praty-0.1.0.jar target/praty.jar
mkdir -p /c/praty
cp target/praty.jar /c/praty/
cp scripts/praty.bat /c/praty/
```

## Spring command manual

See [docs/springx/USER_MANUAL.md](docs/springx/USER_MANUAL.md) for `praty spring setup`, `add`, `remove`, and `deps`.
