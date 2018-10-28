package com.github.forestbelton.glua.helper.lua;

import com.github.forestbelton.glua.LuaLexer;
import com.github.forestbelton.glua.LuaParser;
import com.github.forestbelton.glua.model.Module;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Includes helper utilities for parsing Lua files.
 */
public class LuaParsingHelper {
  /**
   * Parse a module as a single block then traverse it with a listener.
   *
   * @param module   The module to parse
   * @param listener The listener to attach
   * @param <A>      The type of the listener
   * @return The listener
   * @throws IOException If an error occurs while reading the file or parsing
   */
  public static <A extends ParseTreeListener> A parseWithListener(Module module, A listener)
      throws IOException {
    final var byteStream = new ByteArrayInputStream(
        module.contents().getBytes(StandardCharsets.UTF_8));
    final var inputStream = CharStreams.fromStream(byteStream);
    final var lexer = new LuaLexer(inputStream);
    final var tokens = new CommonTokenStream(lexer);
    final var parser = new LuaParser(tokens);
    final var context = parser.block();
    final var walker = new ParseTreeWalker();

    walker.walk(listener, context);
    return listener;
  }
}
