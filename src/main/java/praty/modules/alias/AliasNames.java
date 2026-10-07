package praty.modules.alias;

import java.util.Locale;
import java.util.Set;

final class AliasNames {
    private static final Set<String> RESERVED = Set.of(
            "con", "prn", "aux", "nul",
            "com1", "com2", "com3", "com4", "com5", "com6", "com7", "com8", "com9",
            "lpt1", "lpt2", "lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9"
    );

    private AliasNames() {
    }

    static boolean isValid(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        if (!name.matches("[A-Za-z][A-Za-z0-9_-]*")) {
            return false;
        }
        return !RESERVED.contains(name.toLowerCase(Locale.ROOT));
    }
}
