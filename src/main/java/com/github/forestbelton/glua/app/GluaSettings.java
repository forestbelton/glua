package com.github.forestbelton.glua.app;

public class GluaSettings {
    public final String directoryName;
    public final String outputFileName;

    protected GluaSettings(String directoryName, String outputFileName) {
        this.directoryName = directoryName;
        this.outputFileName = outputFileName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String directoryName;
        private String outputFileName;

        public Builder directoryName(String directoryName) {
            this.directoryName = directoryName;
            return this;
        }

        public Builder outputFileName(String outputFileName) {
            this.outputFileName = outputFileName;
            return this;
        }

        public GluaSettings build() {
            return new GluaSettings(directoryName, outputFileName);
        }
    }
}
