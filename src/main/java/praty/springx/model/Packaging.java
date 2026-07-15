package praty.springx.model;

public enum Packaging {
    JAR("jar"),
    WAR("war");

    private final String id;

    Packaging(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return switch (this) {
            case JAR -> "Jar";
            case WAR -> "War";
        };
    }
}
