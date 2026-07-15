package praty.springx.render;

import praty.springx.model.DependencyRef;
import praty.springx.model.ProjectSpec;
import praty.springx.theme.Theme;

import java.io.PrintWriter;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Console renderer for sections, summary cards, and progress bars.
 */
public final class ConsoleRenderer implements Renderer {

    private final PrintWriter out;
    private final Theme theme;

    public ConsoleRenderer(PrintWriter out, Theme theme) {
        this.out = Objects.requireNonNull(out, "out");
        this.theme = Objects.requireNonNull(theme, "theme");
    }

    @Override
    public void section(String title) {
        out.println();
        out.println(theme.divider());
        out.println(theme.accent(title));
        out.println(theme.divider());
        out.flush();
    }

    @Override
    public void divider() {
        out.println(theme.divider());
        out.flush();
    }

    @Override
    public void summaryCard(ProjectSpec spec) {
        out.println();
        out.println(theme.accent("╭─ Project Summary ─────────────────────"));
        row("Project", spec.projectName());
        row("Package", spec.packageName());
        row("Language", spec.language().displayName());
        row("Java", spec.javaVersion().displayName());
        row("Build", spec.buildTool().displayName());
        row("Packaging", spec.packaging().displayName());
        row("Spring Boot", blankToDash(spec.bootVersion()));
        row("Folder", blankToDash(targetFolder(spec)));
        String deps = spec.dependencies().stream()
                .map(DependencyRef::id)
                .collect(Collectors.joining(", "));
        row("Dependencies", deps.isBlank() ? "(none)" : deps);
        out.println(theme.accent("╰───────────────────────────────────────"));
        out.println(theme.secondary("Press Enter to create · Esc to cancel"));
        out.flush();
    }

    @Override
    public void progressBar(String label, double fraction) {
        double clamped = Math.max(0.0, Math.min(1.0, fraction));
        int width = 24;
        int filled = (int) Math.round(clamped * width);
        String bar = "█".repeat(filled) + "░".repeat(width - filled);
        int pct = (int) Math.round(clamped * 100);
        out.printf("%s%n%s %d%%%n", theme.info(label), theme.accent(bar), pct);
        out.flush();
    }

    @Override
    public Theme theme() {
        return theme;
    }

    private void row(String key, String value) {
        out.printf("│ %-12s %s%n", theme.secondary(key), value == null ? "" : value);
    }

    private static String blankToDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    private static String targetFolder(ProjectSpec spec) {
        if (spec.targetDirectory() != null && !spec.targetDirectory().isBlank()) {
            return spec.targetDirectory();
        }
        return spec.projectName();
    }
}
