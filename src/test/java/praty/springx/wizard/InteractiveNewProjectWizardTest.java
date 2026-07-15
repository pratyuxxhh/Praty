package praty.springx.wizard;

import org.junit.jupiter.api.Test;
import praty.springx.config.SpringxConfig;
import praty.springx.core.Result;
import praty.springx.dependency.MockDependencyData;
import praty.springx.model.BuildTool;
import praty.springx.model.JavaVersion;
import praty.springx.model.Language;
import praty.springx.model.Packaging;
import praty.springx.model.ProjectSpec;
import praty.springx.terminal.ScriptedTerminalUi;
import praty.springx.validation.DefaultProjectValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InteractiveNewProjectWizardTest {

    @Test
    void buildsProjectSpecFromScriptedAnswers() {
        ScriptedTerminalUi ui = new ScriptedTerminalUi()
                .enqueueText("my-blog", "com.example.blog")
                .enqueueSelect(
                        0, // Java 21 (default first)
                        0, // Maven
                        0, // Jar
                        0, // boot 3.4.1
                        0  // Java language
                )
                .enqueueMultiSelect(List.of(0, 8)) // web + actuator indexes in mock catalog
                .enqueueConfirm(true);

        InteractiveNewProjectWizard wizard = new InteractiveNewProjectWizard(
                ui,
                new DefaultProjectValidator(),
                SpringxConfig.defaults(),
                MockDependencyData.all(),
                MockDependencyData.bootVersions()
        );

        Result<ProjectSpec> result = wizard.run();
        assertTrue(result.isOk());
        ProjectSpec spec = result.get();
        assertEquals("my-blog", spec.projectName());
        assertEquals("com.example.blog", spec.packageName());
        assertEquals(JavaVersion.JAVA_21, spec.javaVersion());
        assertEquals(BuildTool.MAVEN, spec.buildTool());
        assertEquals(Packaging.JAR, spec.packaging());
        assertEquals("3.4.1", spec.bootVersion());
        assertEquals(Language.JAVA, spec.language());
        assertEquals(2, spec.dependencies().size());
        assertEquals("web", spec.dependencies().get(0).id());
    }

    @Test
    void cancelOnConfirmReturnsFailure() {
        ScriptedTerminalUi ui = new ScriptedTerminalUi()
                .enqueueText("demo", "com.example.demo")
                .enqueueSelect(0, 0, 0, 0, 0)
                .enqueueMultiSelect(List.of())
                .enqueueConfirm(false);

        InteractiveNewProjectWizard wizard = new InteractiveNewProjectWizard(
                ui,
                new DefaultProjectValidator(),
                SpringxConfig.defaults()
        );

        Result<ProjectSpec> result = wizard.run();
        assertTrue(result.isFail());
        assertTrue(result.error().orElseThrow().getMessage().contains("cancelled"));
    }
}
