package com.github.forestbelton.glua.service.resolution;

import com.github.forestbelton.glua.helper.lua.LuaParsingHelper;
import com.github.forestbelton.glua.helper.lua.LuaRequireCallBaseListener;
import com.github.forestbelton.glua.model.Module;
import com.github.forestbelton.glua.model.RequireCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ResolutionServiceImpl implements ResolutionService {

  private static final Logger logger = LogManager.getLogger(ResolutionServiceImpl.class);

  @Override
  public String resolveDependencies(Module module, Map<String, Integer> moduleMap) {
    // TODO: Throw exception instead
    String moduleContents = "<ERROR>";

    logger.info("resolving dependencies for {}", module.fileName);
    try {
      final var fileDirectoryName = Paths.get(module.fileName).getParent().toString();
      final var listener = new LuaRequireCallBaseListener(fileDirectoryName);

      LuaParsingHelper.parseWithListener(module, listener);
      final List<RequireCall> requireCalls = listener.requireCalls();

      final var outputBuilder = new StringBuilder();
      var lastEndIndex = 0;

      for (var requireCall : requireCalls) {
        final var upToHere = module.contents().substring(lastEndIndex, requireCall.charStartIndex);
        outputBuilder.append(upToHere);

        final var moduleIndex = moduleMap.get(requireCall.requirePath);
        if (moduleIndex == null) {
          throw new RuntimeException("unknown module '" + requireCall.requirePath + "'");
        }

        final var resolvedName = String.format("_MODULES[%d]", moduleIndex);
        outputBuilder.append(resolvedName);

        final var callText = module.contents().substring(requireCall.charStartIndex,
            requireCall.charStartIndex + requireCall.requireCallLength);

        logger.info("resolving call {} to {}", callText, resolvedName);
        lastEndIndex = requireCall.charStartIndex + requireCall.requireCallLength + 1;
      }

      final var tail = module.contents().substring(lastEndIndex);
      outputBuilder.append(tail);
      moduleContents = outputBuilder.toString();
    } catch (IOException ex) {
      logger.error("failed to resolve dependencies", ex);
    }

    return moduleContents;
  }
}
