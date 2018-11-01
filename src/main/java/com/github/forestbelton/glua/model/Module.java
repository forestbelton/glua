package com.github.forestbelton.glua.model;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class Module {

  private static final Logger logger = LogManager.getLogger(Module.class);

  public final String fileName;
  protected String contents;

  protected Module(String fileName) {
    this.fileName = fileName;
  }

  public String name() {
    return fileName.replaceAll("[^A-Za-z0-9_]+", "_");
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Module
        && name().equals(((Module)other).name());
  }

  @Override
  public int hashCode() {
    return name().hashCode();
  }

  /**
   * Retrieve the source code of the module.
   *
   * @return The source code
   */
  public String contents() {
    if (this.contents == null) {
      try {
        this.contents = FileUtils.readFileToString(new File(fileName), Charset.defaultCharset());
      } catch (IOException ex) {
        logger.error("failed to retrieve module contents", ex);
      }
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
