package com.github.forestbelton.glua;

import com.github.forestbelton.glua.service.scanner.ScannerService;
import com.github.forestbelton.glua.service.scanner.ScannerServiceImpl;

public class Glua implements Runnable {
    private final GluaSettings settings;
    private final ScannerService scannerService;

    public Glua(GluaSettings settings, ScannerService scannerService) {
        this.settings = settings;
        this.scannerService = scannerService;
    }

    public static void main(String[] args) {
        // TODO: Read from command-line arguments
        final GluaSettings settings = GluaSettings.builder()
                .directoryName("")
                .build();

        final Glua glua = new Glua(settings, new ScannerServiceImpl());
        glua.run();
    }

    @Override
    public void run() {
        for (String fileName : scannerService.scanDirectory(settings.directoryName)) {
        }
    }
}

/*
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
