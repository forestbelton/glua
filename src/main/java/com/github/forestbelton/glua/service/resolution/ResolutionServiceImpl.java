package com.github.forestbelton.glua.service.resolution;

import com.github.forestbelton.glua.model.Module;

import java.util.Map;

public class ResolutionServiceImpl implements ResolutionService {
    @Override
    public String resolveDependencies(Module module, Map<String, Integer> addedModules) {
        return module.contents();
    }
}
