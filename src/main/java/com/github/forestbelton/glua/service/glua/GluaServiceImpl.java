package com.github.forestbelton.glua.service.glua;

import com.github.forestbelton.glua.model.GluaSettings;
import com.github.forestbelton.glua.model.Module;
import com.github.forestbelton.glua.service.dependency.DependencyService;
import com.github.forestbelton.glua.service.resolution.ResolutionService;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import javax.inject.Inject;

public class GluaServiceImpl implements GluaService {

  private static final Logger logger = LogManager.getLogger(GluaServiceImpl.class);

  private final DependencyService dependencyService;
  private final ResolutionService resolutionService;

  /**
   * Create a new {@link GluaServiceImpl}.
   * @param dependencyService The {@link DependencyService} to use
   * @param resolutionService The {@link ResolutionService} to use
   */
  @Inject
  public GluaServiceImpl(DependencyService dependencyService, ResolutionService resolutionService) {
    this.dependencyService = dependencyService;
    this.resolutionService = resolutionService;
  }

  @Override
  public void run(GluaSettings settings) {
    final var dependencyGraph = new SimpleDirectedGraph<Module, DefaultEdge>(DefaultEdge.class);
    final var scanQueue = new LinkedList<Module>();
    final var scanned = new HashSet<Module>();

    final var entryPoint = Module.builder().fileName(settings.entryPoint).build();
    scanQueue.add(entryPoint);

    while (!scanQueue.isEmpty()) {
      final var module = scanQueue.poll();
      final var dependencies = dependencyService.findDependencies(module);

      logger.info("adding source file {}", module.fileName);
      dependencyGraph.addVertex(module);
      scanned.add(module);

      for (Module dependency : dependencies) {
        logger.info("establishing dependency {} -> {}", module.fileName, dependency.fileName);

        dependencyGraph.addVertex(dependency);
        dependencyGraph.addEdge(dependency, module);

        if (!scanned.contains(dependency) && !scanQueue.contains(dependency)) {
          scanQueue.add(dependency);
        }
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

    // NOTE: Last module in the ordering will be the entry point, so it is skipped here
    settings.outputStream.println("local _MODULES = {}\n");
    for (var moduleIndex = 0; moduleIndex < orderedModules.length - 1; ++moduleIndex) {
      final var module = orderedModules[moduleIndex];

      if (!addedModules.containsKey(module.fileName)) {
        settings.outputStream.println("table.insert(_MODULES, (function()");
        settings.outputStream.println(resolutionService.resolveDependencies(module, addedModules));
        settings.outputStream.println("end)())\n");

        addedModules.put(module.fileName, nextModuleId++);
      }
    }

    settings.outputStream.println(resolutionService.resolveDependencies(entryPoint, addedModules));
  }
}
