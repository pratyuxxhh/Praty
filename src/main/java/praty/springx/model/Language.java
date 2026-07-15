package praty.springx.model;

public enum Language {
    JAVA("java"),
    KOTLIN("kotlin"),
    GROOVY("groovy");

    private final String id;

    Language(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return switch (this) {
            case JAVA -> "Java";
            case KOTLIN -> "Kotlin";
            case GROOVY -> "Groovy";
        };
    }
}
