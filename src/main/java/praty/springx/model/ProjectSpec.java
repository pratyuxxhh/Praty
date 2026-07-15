package praty.springx.model;

import java.util.List;

/**
 * Immutable specification for a Spring Boot project to create.
 */
public record ProjectSpec(
        String projectName,
        String packageName,
        String groupId,
        String artifactId,
        JavaVersion javaVersion,
        BuildTool buildTool,
        Packaging packaging,
        String bootVersion,
        Language language,
        List<DependencyRef> dependencies,
        String targetDirectory,
        String description,
        String version
) {
    public ProjectSpec {
        dependencies = dependencies == null ? List.of() : List.copyOf(dependencies);
    }

    public String effectiveArtifactId() {
        String normalized = trimToNull(artifactId);
        if (normalized != null) {
            return normalized;
        }
        String fallback = trimToNull(projectName);
        return fallback == null ? "demo" : normalizeArtifactId(fallback);
    }

    public String effectiveTargetDirectory() {
        String normalized = trimToNull(targetDirectory);
        if (normalized != null) {
            return normalized;
        }
        return effectiveArtifactId();
    }

    public static String normalizeArtifactId(String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return "demo";
        }
        return trimmed.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String projectName = "demo";
        private boolean projectNameSet;
        private String packageName = "com.example.demo";
        private boolean packageNameSet;
        private String groupId = "com.example";
        private boolean groupIdSet;
        private String artifactId = "demo";
        private boolean artifactIdSet;
        private JavaVersion javaVersion = JavaVersion.JAVA_21;
        private BuildTool buildTool = BuildTool.MAVEN;
        private Packaging packaging = Packaging.JAR;
        private String bootVersion = "";
        private Language language = Language.JAVA;
        private List<DependencyRef> dependencies = List.of();
        private String targetDirectory = "";
        private boolean targetDirectorySet;
        private String description = "Demo project for Spring Boot";
        private boolean descriptionSet;
        private String version = "0.0.1-SNAPSHOT";
        private boolean versionSet;

        public Builder projectName(String projectName) {
            this.projectName = projectName;
            this.projectNameSet = true;
            return this;
        }

        public Builder packageName(String packageName) {
            this.packageName = packageName;
            this.packageNameSet = true;
            return this;
        }

        public Builder groupId(String groupId) {
            this.groupId = groupId;
            this.groupIdSet = true;
            return this;
        }

        public Builder artifactId(String artifactId) {
            this.artifactId = artifactId;
            this.artifactIdSet = true;
            return this;
        }

        public Builder javaVersion(JavaVersion javaVersion) {
            this.javaVersion = javaVersion;
            return this;
        }

        public Builder buildTool(BuildTool buildTool) {
            this.buildTool = buildTool;
            return this;
        }

        public Builder packaging(Packaging packaging) {
            this.packaging = packaging;
            return this;
        }

        public Builder bootVersion(String bootVersion) {
            this.bootVersion = bootVersion;
            return this;
        }

        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        public Builder dependencies(List<DependencyRef> dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        public Builder targetDirectory(String targetDirectory) {
            this.targetDirectory = targetDirectory;
            this.targetDirectorySet = true;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            this.descriptionSet = true;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            this.versionSet = true;
            return this;
        }

        public ProjectSpec build() {
            String resolvedArtifactId = artifactIdSet ? trimToNull(artifactId) : null;
            if (resolvedArtifactId == null) {
                String fallbackName = trimToNull(projectNameSet ? projectName : null);
                resolvedArtifactId = normalizeArtifactId(fallbackName == null ? "demo" : fallbackName);
            }

            String resolvedProjectName = trimToNull(projectNameSet ? projectName : null);
            if (resolvedProjectName == null) {
                resolvedProjectName = resolvedArtifactId;
            }

            String resolvedPackageName = packageNameSet ? trimToNull(packageName) : null;
            String resolvedGroupId = groupIdSet ? trimToNull(groupId) : null;
            if (resolvedGroupId == null) {
                if (resolvedPackageName != null) {
                    resolvedGroupId = deriveGroupId(resolvedPackageName);
                }
                if (resolvedGroupId == null) {
                    resolvedGroupId = "com.example";
                }
            }
            if (resolvedPackageName == null) {
                resolvedPackageName = resolvedGroupId + "." + resolvedArtifactId.replace('-', '_');
            }

            String resolvedTargetDirectory = targetDirectorySet ? trimToNull(targetDirectory) : null;
            if (resolvedTargetDirectory == null) {
                resolvedTargetDirectory = resolvedArtifactId;
            }

            String resolvedDescription = descriptionSet ? trimToNull(description) : null;
            if (resolvedDescription == null) {
                resolvedDescription = "Demo project for Spring Boot";
            }

            String resolvedVersion = versionSet ? trimToNull(version) : null;
            if (resolvedVersion == null) {
                resolvedVersion = "0.0.1-SNAPSHOT";
            }

            return new ProjectSpec(
                    resolvedProjectName,
                    resolvedPackageName,
                    resolvedGroupId,
                    resolvedArtifactId,
                    javaVersion,
                    buildTool,
                    packaging,
                    trimToEmpty(bootVersion),
                    language,
                    dependencies,
                    resolvedTargetDirectory,
                    resolvedDescription,
                    resolvedVersion
            );
        }

        private static String deriveGroupId(String packageName) {
            if (packageName == null || packageName.isBlank()) {
                return null;
            }
            int dot = packageName.lastIndexOf('.');
            if (dot > 0) {
                return packageName.substring(0, dot);
            }
            return packageName;
        }
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String trimToEmpty(String value) {
        String trimmed = trimToNull(value);
        return trimmed == null ? "" : trimmed;
    }
}
