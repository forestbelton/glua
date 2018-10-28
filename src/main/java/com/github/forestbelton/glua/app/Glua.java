package com.github.forestbelton.glua.app;

import com.github.forestbelton.glua.model.Module;
import com.github.forestbelton.glua.service.dependency.DependencyService;
import com.github.forestbelton.glua.service.dependency.DependencyServiceImpl;
import com.github.forestbelton.glua.service.resolution.ResolutionService;
import com.github.forestbelton.glua.service.resolution.ResolutionServiceImpl;
import com.github.forestbelton.glua.service.scanner.ScannerService;
import com.github.forestbelton.glua.service.scanner.ScannerServiceImpl;
import org.apache.commons.collections4.IteratorUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

public class Glua implements Runnable {
    private final GluaSettings settings;
    private final ScannerService scannerService;
    private final DependencyService dependencyService;
    private final ResolutionService resolutionService;

    public Glua(GluaSettings settings, ScannerService scannerService, DependencyService dependencyService,
                ResolutionService resolutionService) {
        this.settings = settings;
        this.scannerService = scannerService;
        this.dependencyService = dependencyService;
        this.resolutionService = resolutionService;
    }

    public static void main(String[] args) {
        // TODO: Read from command-line arguments
        final GluaSettings settings = GluaSettings.builder()
                .directoryName("C:\\Users\\case\\Desktop\\sample\\src\\addon_d.ipf\\sample")
                .outputFileName("C:\\Users\\case\\Desktop\\sample\\combined.lua")
                .build();

        final Glua glua = new Glua(settings, new ScannerServiceImpl(), new DependencyServiceImpl(),
                new ResolutionServiceImpl());

        glua.run();
    }

    @Override
    public void run() {
        try (final PrintStream outputStream = new PrintStream(new File(settings.outputFileName))) {
            run(outputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void run(PrintStream outputStream) {
        final Graph<Module, DefaultEdge> dependencyGraph = new SimpleDirectedGraph<>(DefaultEdge.class);

        // TODO: Instead of calling ScannerService::scanDirectory, do DFS on an entrypoint
        for (Module module : scannerService.scanDirectory(settings.directoryName)) {
            final Iterable<Module> dependencies = dependencyService.findDependencies(module);

            System.out.println("adding source file: " + module.fileName);
            dependencyGraph.addVertex(module);
            for (Module dependency : dependencies) {
                dependencyGraph.addVertex(dependency);
                dependencyGraph.addEdge(module, dependency);
                System.out.printf("%s -> %s\n", module.fileName, dependency.fileName);
            }
        }

        final TopologicalOrderIterator<Module, DefaultEdge> ordering = new TopologicalOrderIterator<>(dependencyGraph);
        final Module[] orderedModules = IteratorUtils.toArray(ordering, Module.class);

        final HashMap<String, Integer> addedModules = new HashMap<>();
        int nextModuleId = 0;

        outputStream.println("local _MODULES = {}\n");
        for (int moduleIndex = 0; moduleIndex < orderedModules.length; ++moduleIndex) {
            final Module module = orderedModules[moduleIndex];

            if (!addedModules.containsKey(module.fileName)) {
                outputStream.println("table.insert(_MODULES, (function()\n");
                outputStream.println(resolutionService.resolveDependencies(module, addedModules));
                outputStream.println("end)())\n");

                addedModules.put(module.fileName, nextModuleId++);
            }
        }
    }
}
