package com.github.forestbelton.glua.app;

import picocli.CommandLine;

public class Glua {
  public static void main(String[] args) {
    CommandLine.call(new GluaCommand(), args);
  }
}
