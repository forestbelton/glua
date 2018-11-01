package com.github.forestbelton.glua.app;

import com.github.forestbelton.glua.service.dependency.DependencyService;
import com.github.forestbelton.glua.service.dependency.DependencyServiceImpl;
import com.github.forestbelton.glua.service.glua.GluaService;
import com.github.forestbelton.glua.service.glua.GluaServiceImpl;
import com.github.forestbelton.glua.service.resolution.ResolutionService;
import com.github.forestbelton.glua.service.resolution.ResolutionServiceImpl;
import com.github.forestbelton.glua.service.scanner.ScannerService;
import com.github.forestbelton.glua.service.scanner.ScannerServiceImpl;
import dagger.Module;
import dagger.Provides;

@Module
public class GluaModule {
  @Provides static DependencyService dependencyService() {
    return new DependencyServiceImpl();
  }

  @Provides static ResolutionService resolutionService() {
    return new ResolutionServiceImpl();
  }

  @Provides static ScannerService scannerService() {
    return new ScannerServiceImpl();
  }

  @Provides static GluaService gluaService(DependencyService dependencyService, ResolutionService resolutionService, ScannerService scannerService) {
    return new GluaServiceImpl(scannerService, dependencyService, resolutionService);
  }
}
