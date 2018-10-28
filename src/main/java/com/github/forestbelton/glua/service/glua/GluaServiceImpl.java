package com.github.forestbelton.glua.service.glua;

import com.github.forestbelton.glua.model.GluaSettings;
import com.github.forestbelton.glua.model.Module;
import com.github.forestbelton.glua.service.dependency.DependencyService;
import com.github.forestbelton.glua.service.resolution.ResolutionService;
import com.github.forestbelton.glua.service.scanner.ScannerService;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.HashMap;

public class GluaServiceImpl implements GluaService {

    private static final Logger logger = LogManager.getLogger(GluaServiceImpl.class);

    private final ScannerService scannerService;
    private final DependencyService dependencyService;
    private final ResolutionService resolutionService;

    public GluaServiceImpl(ScannerService scannerService, DependencyService dependencyService,
                           ResolutionService resolutionService) {
        this.scannerService = scannerService;
        this.dependencyService = dependencyService;
        this.resolutionService = resolutionService;
    }

    @Override
    public void run(GluaSettings settings) {
        final Graph<Module, DefaultEdge> dependencyGraph = new SimpleDirectedGraph<>(DefaultEdge.class);

        // TODO: Instead of calling ScannerService::scanDirectory, do DFS on an entrypoint
        for (Module module : scannerService.scanDirectory(settings.directoryName)) {
            final Iterable<Module> dependencies = dependencyService.findDependencies(module);

            logger.info("adding source file {}", module.fileName);
            dependencyGraph.addVertex(module);

            for (Module dependency : dependencies) {
                logger.info("establishing dependency {} -> {}", module.fileName, dependency.fileName);

                dependencyGraph.addVertex(dependency);
                dependencyGraph.addEdge(module, dependency);
            }
        }

        final TopologicalOrderIterator<Module, DefaultEdge> ordering = new TopologicalOrderIterator<>(dependencyGraph);
        final Module[] orderedModules = IteratorUtils.toArray(ordering, Module.class);

        final HashMap<String, Integer> addedModules = new HashMap<>();
        int nextModuleId = 0;

        settings.outputStream.println("local _MODULES = {}\n");
        for (int moduleIndex = 0; moduleIndex < orderedModules.length; ++moduleIndex) {
            final Module module = orderedModules[moduleIndex];

            if (!addedModules.containsKey(module.fileName)) {
                settings.outputStream.println("table.insert(_MODULES, (function()");
                settings.outputStream.println(resolutionService.resolveDependencies(module, addedModules));
                settings.outputStream.println("end)())\n");

                addedModules.put(module.fileName, nextModuleId++);
            }
        }
    }
}
