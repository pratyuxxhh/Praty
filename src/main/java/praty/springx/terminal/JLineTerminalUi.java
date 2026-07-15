package praty.springx.terminal;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import praty.springx.render.ConsoleRenderer;
import praty.springx.render.Renderer;
import praty.springx.theme.Theme;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JLine-backed interactive terminal UI.
 */
public final class JLineTerminalUi implements TerminalUi, AutoCloseable {

    private enum Action {
        UP, DOWN, ENTER, ESCAPE, SPACE, TAB, BACKSPACE, SLASH, CHAR
    }

    private final Terminal terminal;
    private final LineReader lineReader;
    private final PrintWriter writer;
    private final Theme theme;
    private final Renderer renderer;
    private final boolean ownsTerminal;

    public JLineTerminalUi(Terminal terminal, Theme theme, boolean ownsTerminal) {
        this.terminal = Objects.requireNonNull(terminal, "terminal");
        this.theme = Objects.requireNonNull(theme, "theme");
        this.ownsTerminal = ownsTerminal;
        this.writer = terminal.writer();
        this.renderer = new ConsoleRenderer(writer, theme);
        this.lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                .build();
    }

    public static JLineTerminalUi create(Theme theme) {
        try {
            Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .jansi(true)
                    .build();
            return new JLineTerminalUi(terminal, theme, true);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to open interactive terminal.", e);
        }
    }

    public Renderer renderer() {
        return renderer;
    }

    public Theme theme() {
        return theme;
    }

    @Override
    public String textInput(String label, String defaultValue, String placeholder) {
        renderer.section(label);
        if (placeholder != null && !placeholder.isBlank()) {
            writer.println(theme.secondary(placeholder));
        }
        String def = defaultValue == null ? "" : defaultValue;
        if (!def.isBlank()) {
            writer.println(theme.secondary("Default: " + def));
        }
        writer.flush();
        try {
            String line = lineReader.readLine(theme.accent("> "));
            if (line == null || line.isBlank()) {
                return def;
            }
            return line.trim();
        } catch (UserInterruptException e) {
            throw e;
        }
    }

    @Override
    public <T> Optional<T> select(String label, List<T> options, Function<T, String> display) {
        if (options == null || options.isEmpty()) {
            return Optional.empty();
        }
        renderer.section(label);
        writer.println(theme.secondary("↑/↓ move · Enter select · Esc cancel"));
        writer.flush();
        int index = 0;
        int printed = 0;
        Terminal.SignalHandler previous = terminal.handle(Terminal.Signal.INT, Terminal.SignalHandler.SIG_IGN);
        try {
            terminal.enterRawMode();
            KeyMap<Action> keys = navigationKeys();
            BindingReader reader = new BindingReader(terminal.reader());
            while (true) {
                printed = redrawSelect(options, display, index, printed);
                Action action = readAction(reader, keys);
                switch (action) {
                    case UP -> index = (index - 1 + options.size()) % options.size();
                    case DOWN, TAB -> index = (index + 1) % options.size();
                    case ENTER -> {
                        clearLines(printed);
                        writer.println(theme.success(theme.iconSuccess() + " " + display.apply(options.get(index))));
                        writer.flush();
                        return Optional.of(options.get(index));
                    }
                    case ESCAPE -> {
                        clearLines(printed);
                        return Optional.empty();
                    }
                    default -> {
                    }
                }
            }
        } finally {
            terminal.handle(Terminal.Signal.INT, previous);
        }
    }

    @Override
    public <T> List<T> multiSelect(String label, List<T> options, Function<T, String> display) {
        if (options == null || options.isEmpty()) {
            return List.of();
        }
        renderer.section(label);
        writer.println(theme.secondary("↑/↓ move · Space toggle · / search · Enter done · Esc cancel search"));
        writer.flush();
        Set<Integer> selected = new LinkedHashSet<>();
        StringBuilder query = new StringBuilder();
        boolean searching = false;
        int index = 0;
        int printed = 0;
        Terminal.SignalHandler previous = terminal.handle(Terminal.Signal.INT, Terminal.SignalHandler.SIG_IGN);
        try {
            terminal.enterRawMode();
            KeyMap<Action> keys = navigationKeys();
            BindingReader reader = new BindingReader(terminal.reader());
            while (true) {
                List<Integer> visible = visibleIndexes(options, display, query.toString());
                if (visible.isEmpty()) {
                    index = 0;
                } else if (index >= visible.size()) {
                    index = visible.size() - 1;
                }
                printed = redrawMulti(options, display, visible, selected, index, query.toString(), searching, printed);
                Action action = readAction(reader, keys);

                // Space always toggles the highlighted dependency (search mode or not).
                if (action == Action.SPACE) {
                    if (!visible.isEmpty()) {
                        int real = visible.get(index);
                        if (!selected.add(real)) {
                            selected.remove(real);
                        }
                    }
                    continue;
                }
                if (action == Action.UP) {
                    if (!visible.isEmpty()) {
                        index = (index - 1 + visible.size()) % visible.size();
                    }
                    continue;
                }
                if (action == Action.DOWN || action == Action.TAB) {
                    if (!visible.isEmpty()) {
                        index = (index + 1) % visible.size();
                    }
                    continue;
                }
                if (action == Action.ENTER) {
                    if (searching) {
                        searching = false;
                        continue;
                    }
                    clearLines(printed);
                    List<T> result = new ArrayList<>();
                    for (Integer i : selected) {
                        result.add(options.get(i));
                    }
                    writer.println(theme.success(theme.iconSuccess() + " Selected (" + result.size() + ")"));
                    writer.flush();
                    return result;
                }
                if (action == Action.ESCAPE) {
                    if (searching) {
                        searching = false;
                        query.setLength(0);
                        index = 0;
                        continue;
                    }
                    clearLines(printed);
                    return List.of();
                }
                if (action == Action.SLASH && !searching) {
                    searching = true;
                    continue;
                }
                if (searching && action == Action.BACKSPACE) {
                    if (!query.isEmpty()) {
                        query.deleteCharAt(query.length() - 1);
                    }
                    index = 0;
                    continue;
                }
                if (searching && action == Action.CHAR) {
                    String binding = reader.getLastBinding();
                    if (binding != null && !binding.isEmpty()) {
                        char ch = binding.charAt(0);
                        if (ch >= 32 && ch < 127 && ch != ' ') {
                            query.append(ch);
                            index = 0;
                        }
                    }
                }
            }
        } finally {
            terminal.handle(Terminal.Signal.INT, previous);
        }
    }

    @Override
    public boolean confirm(String label, boolean defaultValue) {
        String hint = defaultValue ? "Y/n" : "y/N";
        renderer.section(label);
        writer.println(theme.secondary("default " + hint));
        writer.flush();
        try {
            String line = lineReader.readLine(theme.accent("> "));
            if (line == null || line.isBlank()) {
                return defaultValue;
            }
            String normalized = line.trim().toLowerCase(Locale.ROOT);
            if (normalized.equals("y") || normalized.equals("yes")) {
                return true;
            }
            if (normalized.equals("n") || normalized.equals("no")) {
                return false;
            }
            return defaultValue;
        } catch (UserInterruptException e) {
            return false;
        }
    }

    @Override
    public void info(String message) {
        writer.println(theme.info(theme.iconInfo() + " " + message));
        writer.flush();
    }

    @Override
    public void success(String message) {
        writer.println(theme.success(theme.iconSuccess() + " " + message));
        writer.flush();
    }

    @Override
    public void warn(String message) {
        writer.println(theme.warn(theme.iconWarn() + " " + message));
        writer.flush();
    }

    @Override
    public void error(String message) {
        writer.println(theme.error(theme.iconError() + " " + message));
        writer.flush();
    }

    @Override
    public ProgressHandle progress(String label) {
        return new ProgressHandle() {
            private boolean closed;

            @Override
            public void update(double fraction, String status) {
                if (closed) {
                    return;
                }
                renderer.progressBar(status == null || status.isBlank() ? label : status, fraction);
            }

            @Override
            public void complete(String message) {
                if (closed) {
                    return;
                }
                success(message);
                closed = true;
            }

            @Override
            public void close() {
                closed = true;
            }
        };
    }

    @Override
    public void withSpinner(String label, Runnable work) {
        withSpinner(label, control -> work.run());
    }

    @Override
    public void withSpinner(String label, Consumer<SpinnerControl> work) {
        AtomicBoolean running = new AtomicBoolean(true);
        AtomicReference<String> status = new AtomicReference<>(label);
        Thread spinner = new Thread(() -> {
            char[] frames = {'⠋', '⠙', '⠹', '⠸', '⠼', '⠴', '⠦', '⠧', '⠇', '⠏'};
            int i = 0;
            while (running.get()) {
                writer.print("\r" + theme.accent(frames[i % frames.length] + " " + status.get()) + "   ");
                writer.flush();
                i++;
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            writer.print("\r" + " ".repeat(Math.max(status.get().length() + 6, 24)) + "\r");
            writer.flush();
        }, "springx-spinner");
        spinner.setDaemon(true);
        spinner.start();
        try {
            work.accept(status::set);
        } finally {
            running.set(false);
            try {
                spinner.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void close() {
        if (ownsTerminal) {
            try {
                terminal.close();
            } catch (IOException ignored) {
                // ignore close failures
            }
        }
    }

    private Action readAction(BindingReader reader, KeyMap<Action> keys) {
        Action action = reader.readBinding(keys);
        return action == null ? Action.CHAR : action;
    }

    private KeyMap<Action> navigationKeys() {
        KeyMap<Action> map = new KeyMap<>();
        map.setUnicode(Action.CHAR);
        map.bind(Action.UP, KeyMap.key(terminal, InfoCmp.Capability.key_up), "\u001B[A");
        map.bind(Action.DOWN, KeyMap.key(terminal, InfoCmp.Capability.key_down), "\u001B[B");
        map.bind(Action.ENTER, "\r", "\n");
        map.bind(Action.ESCAPE, "\u001B");
        map.bind(Action.SPACE, " ");
        map.bind(Action.TAB, "\t");
        map.bind(Action.BACKSPACE, "\u007F", "\b");
        map.bind(Action.SLASH, "/");
        map.setNomatch(Action.CHAR);
        return map;
    }

    private <T> int redrawSelect(List<T> options, Function<T, String> display, int index, int previousLines) {
        clearLines(previousLines);
        int lines = 0;
        for (int i = 0; i < options.size(); i++) {
            String marker = i == index ? theme.accent("❯ ") : "  ";
            String text = i == index ? theme.accent(display.apply(options.get(i))) : display.apply(options.get(i));
            writer.println(marker + text);
            lines++;
        }
        writer.flush();
        return lines;
    }

    private <T> int redrawMulti(
            List<T> options,
            Function<T, String> display,
            List<Integer> visible,
            Set<Integer> selected,
            int index,
            String query,
            boolean searching,
            int previousLines
    ) {
        clearLines(previousLines);
        int lines = 0;
        String searchLine = searching || !query.isBlank()
                ? theme.secondary("Search: ") + theme.accent(query + (searching ? "█" : ""))
                : theme.secondary("Press / to search");
        writer.println(searchLine);
        lines++;
        int shown = 0;
        for (int vi = 0; vi < visible.size() && shown < 12; vi++) {
            int real = visible.get(vi);
            boolean isSelected = selected.contains(real);
            boolean cursor = vi == index;
            String check = isSelected ? theme.success("●") : theme.secondary("○");
            String marker = cursor ? theme.accent("❯ ") : "  ";
            String name = display.apply(options.get(real));
            writer.println(marker + check + " " + (cursor ? theme.accent(name) : name));
            lines++;
            shown++;
        }
        if (visible.isEmpty()) {
            writer.println(theme.warn("  No matches"));
            lines++;
        }
        writer.println(theme.secondary("Selected (" + selected.size() + ")"));
        lines++;
        writer.flush();
        return lines;
    }

    private <T> List<Integer> visibleIndexes(List<T> options, Function<T, String> display, String query) {
        List<Integer> indexes = new ArrayList<>();
        String q = query == null ? "" : query.toLowerCase(Locale.ROOT);
        for (int i = 0; i < options.size(); i++) {
            String label = display.apply(options.get(i)).toLowerCase(Locale.ROOT);
            if (q.isBlank() || label.contains(q)) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private void clearLines(int count) {
        for (int i = 0; i < count; i++) {
            writer.print("\u001B[1A");
            writer.print("\u001B[2K");
        }
        writer.flush();
    }
}
