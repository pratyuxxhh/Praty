# Build and install PRATY

This guide shows how to build the PRATY Java application, rename the generated JAR file to a stable name, and copy it to `C:\praty` for easy execution.

1. Build the project:

```bash
mvn clean package
```

- `mvn clean package` removes old build files, compiles the code, and creates the application JAR.
- The generated JAR is stored in `target/`.

2. Rename the generated JAR:

```bash
mv target/praty-0.1.0.jar target/praty.jar
```

- This renames the versioned artifact to `praty.jar` so it is easier to run and copy.

3. Copy the runnable JAR to `C:\praty`:

```bash
cp target/praty.jar C:\praty
```

- Copying the JAR to `C:\praty` makes it easier to use from a shortcut or script.

## Notes

- If your Maven build produces a different version, update the filename accordingly.
- If you want to use a different install directory, replace `C:\praty` with your preferred path.
