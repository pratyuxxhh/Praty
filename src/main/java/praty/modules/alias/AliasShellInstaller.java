package praty.modules.alias;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.concurrent.TimeUnit;

final class AliasShellInstaller {
    private final Path binDir;
    private final boolean updateUserPath;

    AliasShellInstaller(Path binDir, boolean updateUserPath) {
        this.binDir = binDir;
        this.updateUserPath = updateUserPath;
    }

    Path binDir() {
        return binDir;
    }

    boolean install(String name, String realCommand) throws IOException {
        Files.createDirectories(binDir);
        writeCmdWrapper(name, realCommand);
        writeUnixWrapper(name, realCommand);
        if (!updateUserPath) {
            return false;
        }
        return ensureWindowsUserPath();
    }

    private void writeCmdWrapper(String name, String realCommand) throws IOException {
        String body = "@echo off\r\n" + realCommand + " %*\r\n";
        Files.writeString(binDir.resolve(name + ".cmd"), body, StandardCharsets.UTF_8);
    }

    private void writeUnixWrapper(String name, String realCommand) throws IOException {
        Path script = binDir.resolve(name);
        String body = "#!/bin/sh\n" + realCommand + " \"$@\"\n";
        Files.writeString(script, body, StandardCharsets.UTF_8);
        try {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
            Files.setPosixFilePermissions(script, perms);
        } catch (UnsupportedOperationException ignored) {
            // NTFS / Windows has no POSIX permission bits.
        }
    }

    private boolean ensureWindowsUserPath() {
        String os = System.getProperty("os.name", "");
        if (!os.toLowerCase().contains("win")) {
            return false;
        }
        String dir = binDir.toAbsolutePath().toString();
        String escaped = dir.replace("'", "''");
        String script = "$dir = '" + escaped + "'; "
                + "$p = [Environment]::GetEnvironmentVariable('Path','User'); "
                + "if ($null -eq $p) { $p = '' }; "
                + "$parts = @($p -split ';' | ForEach-Object { $_.Trim() } | Where-Object { $_ -ne '' }); "
                + "if ($parts -contains $dir) { Write-Output 'PATH_OK'; exit 0 }; "
                + "$new = if ([string]::IsNullOrWhiteSpace($p)) { $dir } else { $p.TrimEnd(';') + ';' + $dir }; "
                + "[Environment]::SetEnvironmentVariable('Path', $new, 'User'); "
                + "Write-Output 'PATH_UPDATED'";
        try {
            Process process = new ProcessBuilder("powershell", "-NoProfile", "-NonInteractive", "-Command", script)
                    .redirectErrorStream(true)
                    .start();
            boolean finished = process.waitFor(15, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return false;
            }
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            return "PATH_UPDATED".equals(output);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
