package com.github.forestbelton.glua.service.dependency;

import com.github.forestbelton.glua.helper.lua.LuaParsingHelper;
import com.github.forestbelton.glua.helper.lua.LuaRequireCallBaseListener;
import com.github.forestbelton.glua.model.Module;
import com.github.forestbelton.glua.model.RequireCall;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    /** Converts every require() call into a {@link Module}. */
    private static class DependencyListener extends LuaRequireCallBaseListener {
        private final ArrayList<Module> dependencies = new ArrayList<>();

        public DependencyListener(String baseDirectory) {
            super(baseDirectory);
        }

        /**
         * Retrieve all of the dependencies that were located.
         * @return The list of dependencies
         */
        public Iterable<Module> dependencies() {
            return Collections.unmodifiableList(dependencies);
        }

        @Override
        protected void onRequireCall(RequireCall requireCall) {
            final Module dependency = Module.builder().fileName(requireCall.requirePath).build();
            dependencies.add(dependency);
        }
    }
}
