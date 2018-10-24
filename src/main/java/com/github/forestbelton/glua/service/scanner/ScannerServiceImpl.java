package com.github.forestbelton.glua.service.scanner;
import com.github.forestbelton.glua.model.Module;

import java.util.Collections;

public class ScannerServiceImpl implements ScannerService {
    @Override
    public Iterable<Module> scanDirectory(String directoryName) {
        return Collections.emptyList();
    }
}
