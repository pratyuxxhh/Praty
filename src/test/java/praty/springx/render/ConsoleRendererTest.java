package praty.springx.render;

import org.junit.jupiter.api.Test;
import praty.springx.model.BuildTool;
import praty.springx.model.JavaVersion;
import praty.springx.model.Language;
import praty.springx.model.Packaging;
import praty.springx.model.ProjectSpec;
import praty.springx.theme.DefaultTheme;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleRendererTest {

    @Test
    void summaryCardIncludesCoreFields() {
        StringWriter buffer = new StringWriter();
        ConsoleRenderer renderer = new ConsoleRenderer(new PrintWriter(buffer), new DefaultTheme(false));
        ProjectSpec spec = ProjectSpec.builder()
                .projectName("demo")
                .packageName("com.example.demo")
                .javaVersion(JavaVersion.JAVA_21)
                .buildTool(BuildTool.MAVEN)
                .packaging(Packaging.JAR)
                .bootVersion("3.4.1")
                .language(Language.JAVA)
                .build();

        renderer.summaryCard(spec);
        String out = buffer.toString();
        assertTrue(out.contains("demo"));
        assertTrue(out.contains("com.example.demo"));
        assertTrue(out.contains("3.4.1"));
    }
}
