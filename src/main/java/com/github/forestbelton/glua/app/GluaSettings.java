package com.github.forestbelton.glua;

public class GluaSettings {
    public final String directoryName;

    protected GluaSettings(String directoryName) {
        this.directoryName = directoryName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String directoryName;

        public Builder directoryName(String directoryName) {
            this.directoryName = directoryName;
            return this;
        }

        public GluaSettings build() {
            return new GluaSettings(directoryName);
        }
    }
}
