package com.github.forestbelton.glua.service.scanner;

import com.github.forestbelton.glua.model.Module;

public interface ScannerService {
  /**
   * Scans a directory for Lua modules.
   *
   * @param directoryName The directory to scan.
   * @return An @{link Iterable} containing all modules found.
   */
  Iterable<Module> scanDirectory(String directoryName);
}
