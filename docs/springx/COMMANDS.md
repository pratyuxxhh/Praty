# springx Commands

All commands are invoked through the PRATY CLI:

```text
praty spring <action> [args...]
```

## Primary

| Command | Status | Description |
|---------|--------|-------------|
| `praty spring setup` | **Live** | Interactive wizard; creates a Spring Boot project via Initializr HTTP |
| `praty spring new` | Planned | Alias for `setup` |

## Dependency manager

| Command | Status | Description |
|---------|--------|-------------|
| `praty spring add` | **Live** | Interactive dependency browser; edits pom/gradle |
| `praty spring remove` | **Live** | Remove dependencies from the current project |
| `praty spring deps` | V1 partial | Usage history (`ShowDependencies`); expand into catalog browser |
| `praty spring update` | Planned | Update selected dependency versions |

## Diagnostics

| Command | Status | Description |
|---------|--------|-------------|
| `praty spring doctor` | Planned | Detect Java, Maven/Gradle, Git, Docker, network, IDE |
| `praty spring doctor --fix` | Planned | Attempt one-click fixes where possible |
| `praty spring doctor network` | Planned | Network / Initializr connectivity check |
| `praty spring info` | Planned | Project and environment summary |
| `praty spring versions` | Planned | List supported Java / Boot versions |

## Project tooling

| Command | Status | Description |
|---------|--------|-------------|
| `praty spring init` | Planned | Non-interactive / minimal init |
| `praty spring clean` | Planned | Clean build artifacts / local springx temp |
| `praty spring cache clean` | Planned | Clear `~/.praty/springx/cache` |
| `praty spring config` | Planned | View / edit springx config |
| `praty spring upgrade` | Planned | Upgrade project Boot / dependencies guidance |
| `praty spring self-update` | Planned | Update PRATY / springx engine |

## Templates

| Command | Status | Description |
|---------|--------|-------------|
| `praty spring template list` | Planned (Phase 7) | List built-in and plugin templates |
| `praty spring template use <id>` | Planned (Phase 7) | Scaffold from a template |

## Generators

| Command | Status | Description |
|---------|--------|-------------|
| `praty spring generate entity` | Planned (Phase 6) | Generate entity |
| `praty spring generate controller` | Planned (Phase 6) | Generate controller |
| `praty spring generate service` | Planned (Phase 6) | Generate service |
| `praty spring generate repository` | Planned (Phase 6) | Generate repository |
| `praty spring generate dto` | Planned (Phase 6) | Generate DTO |
| `praty spring generate mapper` | Planned (Phase 6) | Generate mapper |
| `praty spring generate exception` | Planned (Phase 6) | Generate exception types |
| `praty spring generate config` | Planned (Phase 6) | Generate configuration class |
| `praty spring generate security` | Planned (Phase 6) | Generate security starter code |
| `praty spring generate docker` | Planned (Phase 6) | Generate Dockerfile / Compose |

## Plugins

| Command | Status | Description |
|---------|--------|-------------|
| `praty spring plugins` | Planned (Phase 8) | List installed plugins |
| `praty spring plugin install <id>` | Planned (Phase 8) | Install a plugin |

## Registration

Commands register in `praty.Main` via `CommandRegistry`:

```java
registry.register("spring", "setup", new NewProjectCommand());
registry.register("spring", "add", new AddCommand());
registry.register("spring", "remove", new RemoveCommand());
registry.register("spring", "deps", new ShowDependencies());
```

Future commands implement `praty.command.Command` (or `praty.springx.commands.SpringCommand`) and register the same way.
