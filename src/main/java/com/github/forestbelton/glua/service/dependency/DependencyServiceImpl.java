package com.github.forestbelton.glua.service.dependency;

import com.github.forestbelton.glua.helper.lua.LuaParsingHelper;
import com.github.forestbelton.glua.model.Module;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

public class DependencyServiceImpl implements DependencyService {
    @Override
    public Iterable<Module> findDependencies(Module module) {
        Iterable<Module> dependencies = Collections.emptyList();

        System.out.println("reading dependencies for: " + module.fileName);
        try {
            final String fileDirectoryName = Paths.get(module.fileName).getParent().toString();
            final DependencyListener listener = new DependencyListener(fileDirectoryName);

            LuaParsingHelper.parseWithListener(module.fileName, listener);
            dependencies = listener.dependencies();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return dependencies;
    }
}
