package com.github.forestbelton.glua.app;

import com.github.forestbelton.glua.model.GluaSettings;
import com.github.forestbelton.glua.service.glua.DaggerGluaFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "glua",
    description = "Resolves calls to require() and combines output into a single Lua file.",
    mixinStandardHelpOptions = true)
public class GluaCommand implements Callable<Void> {
  private static final String GLUA_DEBUG_ENV_VAR = "GLUA_DEBUG";
  private static final Logger logger = LogManager.getLogger(GluaCommand.class);

  @CommandLine.Option(names = {"-e", "--entry"}, required = true,
      description = "The entry point for the application.")
  private String entryPoint;

  @CommandLine.Parameters(index = "0", description = "The output file to generate.",
      defaultValue = "out.lua")
  private String outputFile = "out.lua";

  @CommandLine.Option(names = {"-v", "--verbose"}, description = "Display verbose output.",
      defaultValue = "false")
  private boolean verbose = false;

  @Override
  public Void call() throws Exception {
    final var outputFileName = new File(outputFile).getCanonicalPath();
    final var entryPointPath = new File(entryPoint).getCanonicalPath();
    final var ctx = (LoggerContext) LogManager.getContext(false);
    final var config = ctx.getConfiguration();
    final var loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);

    var level = Level.WARN;
    if (verbose) {
      level = Level.INFO;
    } else if ("true".equals(System.getenv(GLUA_DEBUG_ENV_VAR))) {
      level = Level.DEBUG;
    }

    loggerConfig.setLevel(level);
    ctx.updateLoggers();

    try (final PrintStream outputStream = new PrintStream(new File(outputFileName))) {
      final var settings = GluaSettings.builder()
          .entryPoint(entryPointPath)
          .outputFileName(outputFileName)
          .outputStream(outputStream)
          .build();

      logger.debug("starting glua; entryPoint={}, outputFileName={}", settings.entryPoint,
          settings.outputFileName);

      final var glua = DaggerGluaFactory.create().glua();
      glua.run(settings);
    } catch (IOException ex) {
      logger.error("glua failed to run", ex);
    }

    return null;
  }

  public String entryPoint() {
    return entryPoint;
  }

  public String outputFile() {
    return outputFile;
  }

  public boolean verbose() {
    return verbose;
  }
}
