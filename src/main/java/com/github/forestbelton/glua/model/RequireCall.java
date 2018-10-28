package com.github.forestbelton.glua.model;

public class RequireCall {
  public final String requirePath;
  public final int charStartIndex;
  public final int requireCallLength;

  private RequireCall(String requirePath, int charStartIndex, int requireCallLength) {
    this.requirePath = requirePath;
    this.charStartIndex = charStartIndex;
    this.requireCallLength = requireCallLength;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String requirePath;
    private int charStartIndex;
    private int requireCallLength;

    public Builder requirePath(String requirePath) {
      this.requirePath = requirePath;
      return this;
    }

    public Builder charStartIndex(int charStartIndex) {
      this.charStartIndex = charStartIndex;
      return this;
    }

    public Builder requireCallLength(int requireCallLength) {
      this.requireCallLength = requireCallLength;
      return this;
    }

    public RequireCall build() {
      return new RequireCall(requirePath, charStartIndex, requireCallLength);
    }
  }
}
