package com.github.forestbelton.glua.service;

public interface ScannerService {
    /**
     * Scans a directory for Lua files.
     * @param directoryName The directory to scan.
     * @return An @{link Iterable} containing the full paths of the found Lua files.
     */
    Iterable<String> scanDirectory(String directoryName);
}
