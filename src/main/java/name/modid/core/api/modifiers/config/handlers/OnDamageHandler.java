package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnDamageConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.types.EventType;

public class OnDamageHandler implements ModifierHandler<ModifierConfig.OnDamageConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    EventType type = ((OnDamageConfig) modifiers.get(0).getConfig()).eventType();

    switch (type) {
      default -> {
      }
    }
  }
}
