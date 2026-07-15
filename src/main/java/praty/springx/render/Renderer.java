package praty.springx.render;

import praty.springx.model.ProjectSpec;
import praty.springx.theme.Theme;

/**
 * Renders confirmation cards, section dividers, and progress frames.
 * Implemented in Phase 3.
 */
public interface Renderer {

    void section(String title);

    void divider();

    void summaryCard(ProjectSpec spec);

    void progressBar(String label, double fraction);

    Theme theme();
}
