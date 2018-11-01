package com.github.forestbelton.glua.service.glua;

import com.github.forestbelton.glua.model.GluaSettings;
import com.github.forestbelton.glua.model.Module;
import com.github.forestbelton.glua.service.dependency.DependencyService;
import com.github.forestbelton.glua.service.resolution.ResolutionService;
import com.github.forestbelton.glua.service.scanner.ScannerService;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.HashMap;
import javax.inject.Inject;

public class GluaServiceImpl implements GluaService {

  private static final Logger logger = LogManager.getLogger(GluaServiceImpl.class);

  private final ScannerService scannerService;
  private final DependencyService dependencyService;
  private final ResolutionService resolutionService;

  /**
   * Create a new {@link GluaServiceImpl}.
   * @param scannerService The {@link ScannerService} to use
   * @param dependencyService The {@link DependencyService} to use
   * @param resolutionService The {@link ResolutionService} to use
   */
  @Inject
  public GluaServiceImpl(ScannerService scannerService, DependencyService dependencyService,
                         ResolutionService resolutionService) {
    this.scannerService = scannerService;
    this.dependencyService = dependencyService;
    this.resolutionService = resolutionService;
  }

  @Override
  public void run(GluaSettings settings) {
    final var dependencyGraph = new SimpleDirectedGraph<Module, DefaultEdge>(DefaultEdge.class);

    // TODO: Instead of calling ScannerService::scanDirectory, do DFS on an entrypoint
    for (var module : scannerService.scanDirectory(settings.directoryName)) {
      final var dependencies = dependencyService.findDependencies(module);

      logger.info("adding source file {}", module.fileName);
      dependencyGraph.addVertex(module);

      for (Module dependency : dependencies) {
        logger.info("establishing dependency {} -> {}", module.fileName, dependency.fileName);

        dependencyGraph.addVertex(dependency);
        dependencyGraph.addEdge(dependency, module);
      }
    }

    final var ordering = new TopologicalOrderIterator<>(dependencyGraph);
    final var orderedModules = IteratorUtils.toArray(ordering, Module.class);

    logger.debug("dependency graph:");
    for (var edge : dependencyGraph.edgeSet()) {
      var source = dependencyGraph.getEdgeSource(edge);
      var target = dependencyGraph.getEdgeTarget(edge);

      logger.info("{} -> {}", source.name(), target.name());
    }

    final var addedModules = new HashMap<String, Integer>();
    var nextModuleId = 0;

    settings.outputStream.println("local _MODULES = {}\n");
    for (var moduleIndex = 0; moduleIndex < orderedModules.length; ++moduleIndex) {
      final var module = orderedModules[moduleIndex];

      if (!addedModules.containsKey(module.fileName)) {
        settings.outputStream.println("table.insert(_MODULES, (function()");
        settings.outputStream.println(resolutionService.resolveDependencies(module, addedModules));
        settings.outputStream.println("end)())\n");

        addedModules.put(module.fileName, nextModuleId++);
      }
    }
  }
}
