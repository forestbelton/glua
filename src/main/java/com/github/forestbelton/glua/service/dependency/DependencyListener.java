package com.github.forestbelton.glua.service.dependency;

import com.github.forestbelton.glua.LuaBaseListener;
import com.github.forestbelton.glua.LuaParser;
import com.github.forestbelton.glua.model.Module;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependencyListener extends LuaBaseListener {
    private static final Pattern requirePattern = Pattern.compile("^require\\([\"'](\\.{1,2}/[^\"'\\\\]+)[\"']\\)$");

    private final String currentDirectory;
    private final ArrayList<Module> dependencies;

    public DependencyListener(String currentDirectory) {
        this.currentDirectory = currentDirectory;
        this.dependencies = new ArrayList<>();
    }

    public Iterable<Module> dependencies() {
        return dependencies;
    }

    @Override
    public void enterExp(LuaParser.ExpContext context) {
        super.enterExp(context);

        final String contents = context.getText();
        final Matcher matcher = requirePattern.matcher(contents);

        if (matcher.matches()) {
            final String requirePath = matcher.group(1).replace('/', '\\');
            final String requireFilePath = String.format("%s\\%s.lua", currentDirectory, requirePath);

            try {
                final String canonicalRequirePath = new File(requireFilePath).getCanonicalPath();
                final Module dependencyModule = Module.builder().fileName(canonicalRequirePath).build();

                dependencies.add(dependencyModule);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
