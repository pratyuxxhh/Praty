package praty.springx.template;

import praty.springx.core.Result;

import java.util.List;

/**
 * Lists and resolves templates. Implemented in Phase 7.
 */
public interface TemplateCatalog {

    List<Template> list();

    Result<Template> find(String id);
}
