package praty.springx.model;

public enum JavaVersion {
    JAVA_17("17"),
    JAVA_21("21"),
    JAVA_24("24"),
    JAVA_25("25");

    private final String id;

    JavaVersion(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return "Java " + id;
    }
}
