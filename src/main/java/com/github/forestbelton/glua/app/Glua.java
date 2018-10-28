package com.github.forestbelton.glua.app;

import com.github.forestbelton.glua.model.GluaSettings;
import com.github.forestbelton.glua.service.dependency.DependencyServiceImpl;
import com.github.forestbelton.glua.service.glua.GluaService;
import com.github.forestbelton.glua.service.glua.GluaServiceImpl;
import com.github.forestbelton.glua.service.resolution.ResolutionServiceImpl;
import com.github.forestbelton.glua.service.scanner.ScannerServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

        @CommandLine.Parameters(index = "0", description = "The source directory to scan.", defaultValue = ".")
        private String directoryName;

        @CommandLine.Parameters(index = "1", description = "The output file to generate.", defaultValue = "out.lua")
        private String outputFile;

        @Override
        public Void call() throws Exception {
            final String outputFileName = new File(outputFile).getCanonicalPath();
            final String searchDirectoryName = new File(directoryName).getCanonicalPath();

            try (final PrintStream outputStream = new PrintStream(new File(outputFileName))) {
                final GluaSettings settings = GluaSettings.builder()
                        .directoryName(searchDirectoryName)
                        .outputFileName(outputFileName)
                        .outputStream(outputStream)
                        .build();

                final GluaService glua = new GluaServiceImpl(new ScannerServiceImpl(), new DependencyServiceImpl(),
                        new ResolutionServiceImpl());

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
