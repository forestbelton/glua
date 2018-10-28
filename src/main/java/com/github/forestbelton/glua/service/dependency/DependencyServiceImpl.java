package com.github.forestbelton.glua.service.dependency;

import com.github.forestbelton.glua.LuaLexer;
import com.github.forestbelton.glua.LuaParser;
import com.github.forestbelton.glua.model.Module;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

public class DependencyServiceImpl implements DependencyService {
    @Override
    public Iterable<Module> findDependencies(Module module) {
        Iterable<Module> dependencies = Collections.emptyList();

        try {
            System.out.println("reading dependencies for: " + module.fileName);

            final LuaLexer lexer = new LuaLexer(CharStreams.fromFileName(module.fileName));
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final LuaParser parser = new LuaParser(tokens);
            final LuaParser.BlockContext context = parser.block();

            final ParseTreeWalker walker = new ParseTreeWalker();
            final String fileDirectoryName = Paths.get(module.fileName).getParent().toString();
            final DependencyListener listener = new DependencyListener(fileDirectoryName);

            walker.walk(listener, context);
            dependencies = listener.dependencies();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return dependencies;
    }
}
