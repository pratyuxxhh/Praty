# Springx Summary

- Springx is the internal Spring Boot workflow engine inside PRATY.
- Main entry point: `praty spring setup`.
- Live commands today: `setup`, `add`, `remove`, and `deps`.
- `setup` uses Spring Initializr over HTTP, not the Spring CLI.
- `add` and `remove` edit `pom.xml` or `build.gradle` in the current project.
- The wizard flow is: project name, package name, Java version, build tool, packaging, Spring Boot version, language, dependencies, confirm.
- UI uses JLine for interactive terminal screens, search, multi-select, and progress.
- Springx keeps metadata in a local cache so it can work offline after the first fetch.
- User config lives under `~/.praty/springx/`.
- Error handling uses `SpringxException` with message, reason, and suggestion.
- The code is split into presentation, application, domain, and infrastructure layers.
- Project creation goes through `NewProjectWizard` -> `ProjectCreateService` -> `HttpInitializrClient` -> `ZipProjectGenerator`.
- Install on Windows with `mvn clean package`, copy the shaded JAR to `C:\praty`, and add that folder to `PATH`.
- Run `praty spring setup` from outside the PRATY repo so the new project is created in the right folder.
