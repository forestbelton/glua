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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A listener that provides a callback for require() calls. */
public class LuaRequireCallBaseListener extends LuaBaseListener {

    private static final Logger logger = LogManager.getLogger(LuaRequireCallBaseListener.class);
    private static final Pattern requirePattern = Pattern.compile("^require\\([\"'](\\.{1,2}/[^\"'\\\\]+)[\"']\\)$");

    private final String baseDirectory;
    private final ArrayList<RequireCall> requireCalls;

    public LuaRequireCallBaseListener(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        this.requireCalls = new ArrayList<>();
    }

    /**
     * Called whenever a require() call is found.
     * @param requireCall The require() call that was located.
     */
    protected void onRequireCall(RequireCall requireCall) {
    }

    /**
     * Returns all the require() calls that were found.
     * @return
     */
    public List<RequireCall> requireCalls() {
        return Collections.unmodifiableList(requireCalls);
    }

    @Override
    public void enterExp(LuaParser.ExpContext context) {
        super.enterExp(context);

        final String contents = context.getText();
        final Matcher matcher = requirePattern.matcher(contents);
        final int matchStartIndex = context.start.getStartIndex();

        if (matcher.matches()) {
            final String match = matcher.group(1);
            final String requirePath = match.replace('/', '\\');
            final String requireFilePath = String.format("%s\\%s.lua", baseDirectory, requirePath);

            try {
                final String canonicalRequirePath = new File(requireFilePath).getCanonicalPath();
                final RequireCall requireCall = RequireCall.builder()
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
