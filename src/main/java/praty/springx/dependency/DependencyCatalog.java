package praty.springx.dependency;

import praty.springx.core.Result;
import praty.springx.model.Dependency;
import praty.springx.model.DependencyRef;

import java.util.List;

/**
 * Searchable dependency catalog and recommendations.
 * Implemented in Phase 5.
 */
public interface DependencyCatalog {

    List<Dependency> all();

    List<Dependency> search(String query);

    List<Dependency> recommend(List<DependencyRef> selected);

    Result<Dependency> findById(String id);
}
