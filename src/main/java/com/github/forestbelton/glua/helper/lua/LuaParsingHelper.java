package com.github.forestbelton.glua.helper.lua;

import com.github.forestbelton.glua.LuaLexer;
import com.github.forestbelton.glua.LuaParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

/** Helper utilities while parsing Lua files */
public class LuaParsingHelper {
    /**
     * Parse a Lua file as a single block then traverse it with a listener.
     * @param fileName The name of the file to parse
     * @param listener The listener to attach
     * @param <A> The type of the listener
     * @throws IOException If an error occurs while reading the file or parsing
     * @return The listener
     */
    public static <A extends ParseTreeListener> A parseWithListener(String fileName, A listener) throws IOException {
        final LuaLexer lexer = new LuaLexer(CharStreams.fromFileName(fileName));
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final LuaParser parser = new LuaParser(tokens);
        final LuaParser.BlockContext context = parser.block();

        final ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, context);

        return listener;
    }
}
