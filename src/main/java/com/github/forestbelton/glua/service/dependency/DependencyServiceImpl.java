package com.github.forestbelton.glua.service.dependency;

import com.github.forestbelton.glua.model.Module;

import java.util.Collections;

public class DependencyServiceImpl implements DependencyService {
    @Override
    public Iterable<Module> findDependencies(Module module) {
        return Collections.emptyList();
    }
}

/*

TODO: Salvage the parts into this implementation

import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Glua extends LuaBaseListener {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            usage(args[0]);
            System.exit(1);
        }

        new Gluer(args[2]).process();
    }

    protected static void usage(String programName) {
        System.err.printf(""
            + "usage: %s <path> <main-file>\n"
            + "<path>\tproject source root directory\n"
            + "<main-file>\tfilename of the application entry point\n",
            programName);
    }

    protected static class Gluer {
        protected final String entryPoint;

        public Gluer(String entryPoint) {
            this.entryPoint = entryPoint;
        }

        public void process() throws IOException {
            this.process(entryPoint);
        }

        protected void process(String fileName) throws IOException {
            final CharStream inputStream = CharStreams.fromFileName(fileName);
            final com.github.forestbelton.glua.LuaLexer lexer = new LuaLexer(inputStream);
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final LuaParser parser = new LuaParser(tokens);

            // TODO: Transform and store
        }
    }
}
*/
