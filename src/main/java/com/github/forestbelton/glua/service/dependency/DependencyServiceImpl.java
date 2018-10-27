package com.github.forestbelton.glua.service.dependency;

import com.github.forestbelton.glua.LuaBaseListener;
import com.github.forestbelton.glua.LuaLexer;
import com.github.forestbelton.glua.LuaParser;
import com.github.forestbelton.glua.model.Module;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.Collections;

public class DependencyServiceImpl implements DependencyService {
    @Override
    public Iterable<Module> findDependencies(Module module) {
        Iterable<Module> dependencies = Collections.emptyList();

        try {
            final LuaLexer lexer = new LuaLexer(CharStreams.fromFileName(module.fileName));
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final LuaParser parser = new LuaParser(tokens);
            final LuaParser.ChunkContext fileChunk = parser.chunk();

            final ParseTreeWalker walker = new ParseTreeWalker();
            final DependencyListener listener = new DependencyListener();

            walker.walk(listener, fileChunk);
            dependencies = listener.dependencies();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return dependencies;
    }

    private static class DependencyListener extends LuaBaseListener {
        public Iterable<Module> dependencies() {
            return Collections.emptyList();
        }

        @Override
        public void enterFunctioncall(LuaParser.FunctioncallContext ctx) {
            final String functionName = ctx.varOrExp().var().NAME().getText();

            System.out.printf("found function call, function = %s\n", functionName);
        }
    }
}
