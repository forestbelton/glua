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
            final String fileDirectoryName = Paths.get(module.fileName).getParent().toString();
            final LuaRequireCallBaseListener listener = new LuaRequireCallBaseListener(fileDirectoryName);

            LuaParsingHelper.parseWithListener(module, listener);
            final List<RequireCall> requireCalls = listener.requireCalls();

            final StringBuilder outputBuilder = new StringBuilder();
            int lastEndIndex = 0;

            for (RequireCall requireCall : requireCalls) {
                final String upToHere = module.contents().substring(lastEndIndex, requireCall.charStartIndex);
                final String resolvedName = String.format("_MODULE[%d]", moduleMap.get(requireCall.requirePath));

                outputBuilder.append(upToHere);
                outputBuilder.append(resolvedName);

                final String callText = module.contents().substring(requireCall.charStartIndex, requireCall.charStartIndex
                        + requireCall.requireCallLength);

                logger.info("resolving call {} to {}", callText, resolvedName);
                lastEndIndex = requireCall.charStartIndex + requireCall.requireCallLength + 1;
            }

            final String tail = module.contents().substring(lastEndIndex);
            outputBuilder.append(tail);
            moduleContents = outputBuilder.toString();
        } catch (IOException ex) {
            logger.error("failed to resolve dependencies", ex);
        }

        return moduleContents;
    }
}
