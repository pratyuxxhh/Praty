package praty.modules.alias;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AliasStoreTest {

    @TempDir
    Path tempDir;

    @Test
    void loadReturnsEmptyWhenFileMissing() throws Exception {
        AliasStore store = new AliasStore(tempDir.resolve("aliases.json"));
        assertTrue(store.load().isEmpty());
    }

    @Test
    void saveAndLoadRoundTrip() throws Exception {
        AliasStore store = new AliasStore(tempDir.resolve("aliases.json"));
        store.save(Map.of("gs", "git status"));

        Map<String, String> loaded = store.load();
        assertEquals("git status", loaded.get("gs"));
    }

    @Test
    void installerWritesCmdAndUnixWrappers() throws Exception {
        Path bin = tempDir.resolve("bin");
        AliasShellInstaller installer = new AliasShellInstaller(bin, false);
        installer.install("gs", "git status");

        String cmd = Files.readString(bin.resolve("gs.cmd"));
        String sh = Files.readString(bin.resolve("gs"));
        assertTrue(cmd.contains("git status %*"));
        assertTrue(sh.contains("git status \"$@\""));
        assertTrue(sh.startsWith("#!/bin/sh"));
    }

    @Test
    void rejectsInvalidAliasNames() {
        assertFalse(AliasNames.isValid(""));
        assertFalse(AliasNames.isValid("-l"));
        assertFalse(AliasNames.isValid("nul"));
        assertFalse(AliasNames.isValid("my command"));
        assertTrue(AliasNames.isValid("gs"));
        assertTrue(AliasNames.isValid("my-command"));
    }
}
