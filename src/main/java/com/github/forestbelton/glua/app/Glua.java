package com.github.forestbelton.glua.app;

import com.github.forestbelton.glua.model.GluaSettings;
import com.github.forestbelton.glua.service.dependency.DependencyServiceImpl;
import com.github.forestbelton.glua.service.glua.GluaService;
import com.github.forestbelton.glua.service.glua.GluaServiceImpl;
import com.github.forestbelton.glua.service.resolution.ResolutionServiceImpl;
import com.github.forestbelton.glua.service.scanner.ScannerServiceImpl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class Glua {

    public static void main(String[] args) {
        // TODO: Read settings from command-line arguments
        final String outputFileName = "C:\\Users\\case\\Desktop\\sample\\combined.lua";

        try (final PrintStream outputStream = new PrintStream(new File(outputFileName))) {
            final GluaSettings settings = GluaSettings.builder()
                    .directoryName("C:\\Users\\case\\Desktop\\sample\\src\\addon_d.ipf\\sample")
                    .outputFileName(outputFileName)
                    .outputStream(outputStream)
                    .build();

            final GluaService glua = new GluaServiceImpl(new ScannerServiceImpl(), new DependencyServiceImpl(),
                    new ResolutionServiceImpl());

            glua.run(settings);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
