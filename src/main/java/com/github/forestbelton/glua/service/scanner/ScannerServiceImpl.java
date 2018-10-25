package com.github.forestbelton.glua.service.scanner;
import com.github.forestbelton.glua.model.Module;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Iterator;
import java.util.stream.Collectors;

public class ScannerServiceImpl implements ScannerService {
    @Override
    public Iterable<Module> scanDirectory(String directoryName) {
        final Iterator<File> files = FileUtils.iterateFiles(new File(directoryName), new String[]{"lua"}, true);
        return IteratorUtils.toList(files).stream().map(file -> Module.builder().fileName(file.getPath()).build())
                .collect(Collectors.toList());
    }
}
