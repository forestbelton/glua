package com.github.forestbelton.glua.service.resolution;

import com.github.forestbelton.glua.helper.lua.LuaParsingHelper;
import com.github.forestbelton.glua.helper.lua.LuaRequireCallBaseListener;
import com.github.forestbelton.glua.model.Module;
import com.github.forestbelton.glua.model.RequireCall;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ResolutionServiceImpl implements ResolutionService {
    @Override
    public String resolveDependencies(Module module, Map<String, Integer> moduleMap) {
        // TODO: Throw exception instead
        String moduleContents = "<ERROR>";

        System.out.println("resolving dependencies for: " + module.fileName);
        try {
            final String fileDirectoryName = Paths.get(module.fileName).getParent().toString();
            final LuaRequireCallBaseListener listener = new LuaRequireCallBaseListener(fileDirectoryName);

            LuaParsingHelper.parseWithListener(module.fileName, listener);
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

                System.out.println("resolving call " + callText + " to " + resolvedName);

                lastEndIndex = requireCall.charStartIndex + requireCall.requireCallLength + 1;
            }

            final String tail = module.contents().substring(lastEndIndex);
            outputBuilder.append(tail);
            moduleContents = outputBuilder.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return moduleContents;
    }
}
