package com.github.forestbelton.glua.app;

import com.github.forestbelton.glua.model.Module;
import com.github.forestbelton.glua.service.dependency.DependencyService;
import com.github.forestbelton.glua.service.dependency.DependencyServiceImpl;
import com.github.forestbelton.glua.service.scanner.ScannerService;
import com.github.forestbelton.glua.service.scanner.ScannerServiceImpl;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

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
                .directoryName("")
                .build();

        final Glua glua = new Glua(settings, new ScannerServiceImpl(), new DependencyServiceImpl());
        glua.run();
    }

    @Override
    public void run() {
        final Graph<Module, DefaultEdge> dependencyGraph = new SimpleGraph<>(DefaultEdge.class);

        for (Module module : scannerService.scanDirectory(settings.directoryName)) {
            final Iterable<Module> dependencies = dependencyService.findDependencies(module);

            dependencyGraph.addVertex(module);
            for (Module dependency : dependencies) {
                dependencyGraph.addVertex(dependency);
                dependencyGraph.addEdge(module, dependency);
            }
        }

        final TopologicalOrderIterator<Module, DefaultEdge> ordering = new TopologicalOrderIterator<>(dependencyGraph);
        while (ordering.hasNext()) {
            final Module module = ordering.next();

            // TODO: Replace all require() calls with references to state
            // TODO: Wrap module contents and put in state
        }
    }
}
