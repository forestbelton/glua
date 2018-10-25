package com.github.forestbelton.glua.model;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class Module {
    public final String fileName;
    protected String contents;

    protected Module(String fileName) {
        this.fileName = fileName;
    }

    public String name() {
        return fileName.replaceAll("[^A-Za-z0-9_]+", "_");
    }

    public String contents() {
        if (this.contents == null) {
            try {
                this.contents = FileUtils.readFileToString(new File(fileName), Charset.defaultCharset());
            } catch (IOException ex) {}
        }

        return this.contents;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fileName;

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Module build() {
            return new Module(fileName);
        }
    }
}
