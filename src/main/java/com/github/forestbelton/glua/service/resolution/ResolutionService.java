package com.github.forestbelton.glua.service.resolution;

import com.github.forestbelton.glua.model.Module;

import java.util.Map;

public interface ResolutionService {
  /**
   * Resolve a module's dependencies according to a mapping.
   *
   * @param module    The module to resolve the dependencies of
   * @param moduleMap The mapping of dependency names to their entries in the global map
   * @return The source code of the resolved module
   */
  String resolveDependencies(Module module, Map<String, Integer> moduleMap);
}
