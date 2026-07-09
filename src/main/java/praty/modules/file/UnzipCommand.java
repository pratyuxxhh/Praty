package praty.modules.file;

import praty.command.Command;
import praty.command.CommandContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipCommand implements Command {
    @Override
    public void execute(CommandContext ctx) {
        List<String> args = ctx.arguments();

        String zipFile = args.get(0);
        String destinationDir = args.get(1);
        try {
        Path destDir = Paths.get(destinationDir);
        Files.createDirectories(destDir);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = destDir.resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());

                    try (OutputStream os = Files.newOutputStream(filePath)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            os.write(buffer, 0, len);
                        }
                    }
                }

                zis.closeEntry();
            }
        }

        System.out.println("Unzip completed successfully.");
    } catch (IOException e) {
        System.out.println("Unzip failed: " + e.getMessage());
    }
    }
}
