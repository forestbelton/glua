package com.github.forestbelton.glua.app;

import com.github.forestbelton.glua.model.Module;
import com.github.forestbelton.glua.service.dependency.DependencyService;
import com.github.forestbelton.glua.service.dependency.DependencyServiceImpl;
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

public class Glua implements Runnable {
    private final GluaSettings settings;
    private final ScannerService scannerService;
    private final DependencyService dependencyService;

    public Glua(GluaSettings settings, ScannerService scannerService, DependencyService dependencyService) {
        this.settings = settings;
        this.scannerService = scannerService;
        this.dependencyService = dependencyService;
    }

    public static void main(String[] args) {
        // TODO: Read from command-line arguments
        final GluaSettings settings = GluaSettings.builder()
                .directoryName("C:\\Users\\case\\Desktop\\sample\\src\\addon_d.ipf\\sample")
                .outputFileName("C:\\Users\\case\\Desktop\\sample\\combined.lua")
                .build();

        final Glua glua = new Glua(settings, new ScannerServiceImpl(), new DependencyServiceImpl());
        glua.run();
    }

    @Override
    public void run() {
        try (final PrintStream outputStream = new PrintStream(new File(settings.outputFileName))) {
            run(outputStream);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
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
            }
        }

        final TopologicalOrderIterator<Module, DefaultEdge> ordering = new TopologicalOrderIterator<>(dependencyGraph);
        final Module[] orderedModules = IteratorUtils.toArray(ordering, Module.class);

        outputStream.println("local _MODULES = {}\n");
        for (int moduleIndex = 0; moduleIndex < orderedModules.length; ++moduleIndex) {
            final Module module = orderedModules[moduleIndex];

            outputStream.println("table.insert(_MODULES, (function()\n");
            outputStream.println(module.contents());
            outputStream.println("end)())\n");

            // TODO: Replace all require() calls with references to state
            // TODO: Wrap module contents and put in state
        }
    }
}
