package com.github.forestbelton.glua.service.resolution;

import com.github.forestbelton.glua.model.Module;

import java.util.Map;

public interface ResolutionService {
    String resolveDependencies(Module module, Map<String, Integer> addedModules);
}
