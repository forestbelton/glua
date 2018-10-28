package com.github.forestbelton.glua.helper.lua;

import com.github.forestbelton.glua.LuaBaseListener;
import com.github.forestbelton.glua.LuaParser;
import com.github.forestbelton.glua.model.RequireCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A listener that provides a callback for require() calls.
 */
public class LuaRequireCallBaseListener extends LuaBaseListener {

  private static final Logger logger = LogManager.getLogger(LuaRequireCallBaseListener.class);
  private static final Pattern requirePattern = Pattern.compile(
      "^require\\([\"'](\\.{1,2}/[^\"'\\\\]+)[\"']\\)$");

  private final String baseDirectory;
  private final ArrayList<RequireCall> requireCalls;

  public LuaRequireCallBaseListener(String baseDirectory) {
    this.baseDirectory = baseDirectory;
    this.requireCalls = new ArrayList<>();
  }

  /**
   * Called whenever a require() call is found.
   *
   * @param requireCall The require() call that was located
   */
  protected void onRequireCall(RequireCall requireCall) {
  }

  /**
   * Returns all the require() calls that were found.
   *
   * @return All found require() calls
   */
  public List<RequireCall> requireCalls() {
    return Collections.unmodifiableList(requireCalls);
  }

  @Override
  public void enterExp(LuaParser.ExpContext context) {
    super.enterExp(context);

    final var contents = context.getText();
    final var matcher = requirePattern.matcher(contents);
    final var matchStartIndex = context.start.getStartIndex();

    if (matcher.matches()) {
      final var match = matcher.group(1);
      final var requirePath = match.replace('/', '\\');
      final var requireFilePath = String.format("%s\\%s.lua", baseDirectory, requirePath);

      try {
        final var canonicalRequirePath = new File(requireFilePath).getCanonicalPath();
        final var requireCall = RequireCall.builder()
            .requirePath(canonicalRequirePath)
            .charStartIndex(matchStartIndex)
            .requireCallLength(contents.length())
            .build();

        requireCalls.add(requireCall);
        this.onRequireCall(requireCall);
      } catch (IOException ex) {
        logger.error("failed to locate require() call", ex);
      }
    }
  }
}
