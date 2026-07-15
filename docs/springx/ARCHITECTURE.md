# springx Architecture

Internal product name for the Spring Boot developer experience engine inside PRATY.

**Public entry point:** `praty spring setup` creates a new Spring Boot project.

## Goals

- Premium interactive CLI DX (arrow keys, search, multi-select, progress)
- Clean Architecture with clear package boundaries
- Extensible via plugins, templates, and hooks
- Fast startup, offline cache, no user-facing stack traces
- Direct Spring Initializr HTTP integration (no `spring` CLI required)

## Layers

```
Presentation   praty.Main → modules.spring adapters → springx.wizard / terminal / render
Application    springx.commands / services
Domain         springx.model / validation / dependency (rules)
Infrastructure springx.network / cache / config / generator
```

**Dependency rule:** presentation → application → domain ← infrastructure.  
Domain (`model`) must never import infrastructure packages.

## Package Map

| Package | Role |
|---------|------|
| `praty.springx.cli` | Spring submodule action routing helpers |
| `praty.springx.commands` | Command implementations (`SetupCommand`, …) |
| `praty.springx.wizard` | Multi-step `NewProjectWizard` and `WizardStep` |
| `praty.springx.terminal` | JLine session and key handling |
| `praty.springx.render` | Panels, progress bars, spinners, summary cards |
| `praty.springx.theme` | Colors, symbols, themes |
| `praty.springx.model` | `ProjectSpec`, `Dependency`, enums |
| `praty.springx.services` | Application services (create, doctor, …) |
| `praty.springx.generator` | Unzip / post-process Initializr artifacts |
| `praty.springx.dependency` | Catalog, search, recommendations, editors |
| `praty.springx.network` | HTTP client for Initializr |
| `praty.springx.cache` | Metadata and offline cache |
| `praty.springx.config` | `~/.praty/springx/config.json` |
| `praty.springx.plugin` | Plugin SPI and hooks |
| `praty.springx.template` | Template SPI |
| `praty.springx.validation` | Name / package validation |
| `praty.springx.core` | `AppContainer`, `Result`, `SpringxException` |
| `praty.springx.utils` | Shared helpers |

Legacy adapters live in `praty.modules.spring` until Phase 4 cutover.

## Command Flow — `praty spring setup`

```
praty.Main
  → CommandRegistry("spring", "setup")
  → NewProjectCommand (adapter)
  → NewProjectWizard (interactive steps)
  → ProjectCreateService.create(ProjectSpec)
  → InitializrClient.downloadProject(...)
  → ProjectGenerator.extract(...)
  → afterCreate hooks
```

## Wizard Steps (Phase 3+)

1. Project name  
2. Package name  
3. Java version  
4. Build tool (Maven / Gradle)  
5. Packaging (Jar / War)  
6. Spring Boot version (from Initializr metadata)  
7. Language (Java / Kotlin / Groovy)  
8. Dependencies (searchable multi-select)  
9. Confirmation summary → generation with progress  

## Config & Cache

| Path | Purpose |
|------|---------|
| `~/.praty/springx/config.json` | Theme, defaults, favorites, recent, telemetry (off by default) |
| `~/.praty/springx/cache/initializr-metadata.json` | Cached Initializr metadata for offline use |
| `~/.praty/dependency-usage.json` | V1 store; migrate into config `usage` (Phase 5+) |

## Error Model

User-facing errors use `SpringxException` with **message**, **reason**, and **suggestion**.  
Never dump stack traces to the terminal.

Example:

```
Unable to download Spring Boot metadata.
Reason: No internet connection.
Suggestion: Run praty spring doctor
```

## Architecture Decision Records

### ADR-001: Keep `praty spring setup` as the create command

Users already run `praty spring setup`. springx is the internal engine name; no separate binary in early phases. Alias `praty spring new` may be added later.

### ADR-002: JLine 3 for terminal UI

Chosen over Lanterna for faster startup (<300 ms goal), CLI-native line redrawing (Vite/shadcn feel), and solid support for arrow keys, Enter, Esc, Tab, and search.

### ADR-003: Spring Initializr over shelling out to `spring`

V1 required the Spring CLI on PATH. V2 calls `https://start.spring.io` via HTTP for portability, progress reporting, and offline-friendly caching.

### ADR-004: Lightweight composition root

`AppContainer` wires dependencies with constructors. No Guice/Spring inside the CLI itself.

### ADR-005: Phase 4 cutover (complete)

`NewProjectCommand` delegates to `SetupCommand`, which uses `ProjectCreateService` + `HttpInitializrClient`. The legacy `spring init` path is no longer used for `praty spring setup`.

## Phase Roadmap

| Phase | Focus |
|-------|--------|
| 1 | Architecture, docs, contracts |
| 2 | Core engine: `AppContainer`, `JsonConfigStore`, `FileMetadataCache`, validators, `ErrorReporter` |
| 3 | JLine widgets (`JLineTerminalUi`), ANSI theme, `InteractiveNewProjectWizard`, `SetupCommand` |
| 4 | Initializr HTTP (`HttpInitializrClient`), `ZipProjectGenerator`, `DefaultProjectCreateService`; `setup` creates without Spring CLI |
| 5 | Dependency manager (`CachedDependencyCatalog`, `MavenBuildFileEditor`, `praty spring add/remove`) |
| 6 | Code generators |
| 7 | Templates |
| 8 | Plugin system |
| 9 | Testing |
| 10 | Full documentation |
