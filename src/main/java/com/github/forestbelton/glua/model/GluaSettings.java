package com.github.forestbelton.glua.model;

import java.io.PrintStream;

public class GluaSettings {
  public final String entryPoint;
  public final String outputFileName;
  public final PrintStream outputStream;

  protected GluaSettings(String entryPoint, String outputFileName, PrintStream outputStream) {
    this.entryPoint = entryPoint;
    this.outputFileName = outputFileName;
    this.outputStream = outputStream;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String directoryName;
    private String outputFileName;
    private PrintStream outputStream;

    public Builder entryPoint(String directoryName) {
      this.directoryName = directoryName;
      return this;
    }

    public Builder outputFileName(String outputFileName) {
      this.outputFileName = outputFileName;
      return this;
    }

    public Builder outputStream(PrintStream outputStream) {
      this.outputStream = outputStream;
      return this;
    }

    public GluaSettings build() {
      return new GluaSettings(directoryName, outputFileName, outputStream);
    }
  }
}
