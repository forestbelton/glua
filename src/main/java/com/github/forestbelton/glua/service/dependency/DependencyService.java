package com.github.forestbelton.glua.service.dependency;

import com.github.forestbelton.glua.model.Module;

public interface DependencyService {
    /**
     * Finds all source-level dependencies by inspecting
     * calls to require() within the Lua module.
     * @param module The Lua module to inspect.
     * @return An {@link Iterable} containing each module dependency.
     */
    Iterable<Module> findDependencies(Module module);
}
