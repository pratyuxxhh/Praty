package praty.springx.model;

public enum BuildTool {
    MAVEN("maven"),
    GRADLE("gradle");

    private final String id;

    BuildTool(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return switch (this) {
            case MAVEN -> "Maven";
            case GRADLE -> "Gradle";
        };
    }
}
