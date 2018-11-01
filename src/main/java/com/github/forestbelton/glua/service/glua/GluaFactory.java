package com.github.forestbelton.glua.service.glua;

import com.github.forestbelton.glua.app.GluaModule;
import dagger.Component;

@Component(modules = GluaModule.class)
public interface GluaFactory {
  GluaService glua();
}
