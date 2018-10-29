package com.github.forestbelton.glua.app;

import com.github.forestbelton.glua.model.GluaSettings;
import com.github.forestbelton.glua.service.glua.DaggerGluaService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.Callable;

public class Glua {

  @CommandLine.Command(name = "glua",
      description = "resolves calls to require() and combines output into a single lua file",
      mixinStandardHelpOptions = true)
  public static class GluaCommand implements Callable<Void> {
    private static final Logger logger = LogManager.getLogger(GluaCommand.class);

    @CommandLine.Parameters(index = "0", description = "The source directory to scan.",
        defaultValue = ".")
    private String directoryName = ".";

    @CommandLine.Parameters(index = "1", description = "The output file to generate.",
        defaultValue = "out.lua")
    private String outputFile = "out.lua";

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Display verbose output.",
        defaultValue = "false")
    private boolean verbose = false;

    @Override
    public Void call() throws Exception {
      final var outputFileName = new File(outputFile).getCanonicalPath();
      final var searchDirectoryName = new File(directoryName).getCanonicalPath();

      if (verbose) {
        final var ctx = (LoggerContext) LogManager.getContext(false);
        final var config = ctx.getConfiguration();
        final var loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);

        loggerConfig.setLevel(Level.INFO);
        ctx.updateLoggers();
      }

      try (final PrintStream outputStream = new PrintStream(new File(outputFileName))) {
        final var settings = GluaSettings.builder()
            .directoryName(searchDirectoryName)
            .outputFileName(outputFileName)
            .outputStream(outputStream)
            .build();

        final var glua = DaggerGluaService.create();
        glua.run(settings);
      } catch (IOException ex) {
        logger.error("glua failed to run", ex);
      }

      return null;
    }
  }

  public static void main(String[] args) {
    CommandLine.call(new GluaCommand(), args);
  }
}
