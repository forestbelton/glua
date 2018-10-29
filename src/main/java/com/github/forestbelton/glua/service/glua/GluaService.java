package com.github.forestbelton.glua.service.glua;

import com.github.forestbelton.glua.app.GluaModule;
import com.github.forestbelton.glua.model.GluaSettings;
import dagger.Component;

@Component(modules = GluaModule.class)
public interface GluaService {
  void run(GluaSettings settings);
}
