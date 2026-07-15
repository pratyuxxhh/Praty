package praty.springx.core;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultAndErrorReporterTest {

    @Test
    void mapAndFlatMapPropagateFailures() {
        Result<String> failed = Result.fail("boom", "reason", "fix it");
        Result<Integer> mapped = failed.map(String::length);
        assertTrue(mapped.isFail());
        assertEquals("boom", mapped.error().orElseThrow().getMessage());

        Result<Integer> ok = Result.ok("hi").flatMap(s -> Result.ok(s.length()));
        assertEquals(2, ok.get());
    }

    @Test
    void errorReporterDoesNotPrintStackTrace() {
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        ErrorReporter reporter = new ErrorReporter(System.out, new PrintStream(err));
        reporter.report(new SpringxException(
                "Unable to download Spring Boot metadata.",
                "No internet connection.",
                "Run praty spring doctor"
        ));

        String output = err.toString();
        assertTrue(output.contains("Unable to download Spring Boot metadata."));
        assertTrue(output.contains("No internet connection."));
        assertTrue(output.contains("Run praty spring doctor"));
        assertFalse(output.contains("at praty."));
    }
}
