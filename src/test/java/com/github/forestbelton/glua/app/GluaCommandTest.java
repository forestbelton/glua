package com.github.forestbelton.glua.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import picocli.CommandLine;

import static org.mockito.Mockito.*;

public class GluaCommandTest extends TestCase {
  public GluaCommandTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(GluaCommandTest.class);
  }

  public void testNotVerboseByDefault() throws Exception {
    final var command = mock(GluaCommand.class);

    doNothing().when(command).call();
    CommandLine.call(command);

    verify(command).call();
    assertFalse("verbose flag not set by default", command.verbose());
  }

  /* TODO: Fix
  
  public void testVerboseWithShortFlag() throws Exception {
    final var command = mock(GluaCommand.class);

    doNothing().when(command).call();
    CommandLine.call(command, "-v");

    verify(command).call();
    assertTrue("verbose flag set by -v", command.verbose());
  }

  public void testVerboseWithLongFlag() throws Exception {
    final var command = mock(GluaCommand.class);
    CommandLine.call(command, "--verbose");

    verify(command).call();
    assertTrue("verbose flag set by --verbose", command.verbose());
  }
  */
}
