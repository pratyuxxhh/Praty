package praty.springx.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectSpecTest {

    @Test
    void builderDerivesConsistentProjectIdentityFromProjectName() {
        ProjectSpec spec = ProjectSpec.builder()
                .projectName("My Blog")
                .build();

        assertEquals("My Blog", spec.projectName());
        assertEquals("my-blog", spec.artifactId());
        assertEquals("my-blog", spec.targetDirectory());
        assertEquals("com.example.my_blog", spec.packageName());
        assertEquals("com.example", spec.groupId());
    }

    @Test
    void explicitIdentityFieldsArePreserved() {
        ProjectSpec spec = ProjectSpec.builder()
                .projectName("My Blog")
                .packageName("org.acme.blog")
                .groupId("org.acme")
                .artifactId("blog-app")
                .targetDirectory("output-folder")
                .build();

        assertEquals("My Blog", spec.projectName());
        assertEquals("org.acme.blog", spec.packageName());
        assertEquals("org.acme", spec.groupId());
        assertEquals("blog-app", spec.artifactId());
        assertEquals("output-folder", spec.targetDirectory());
    }
}