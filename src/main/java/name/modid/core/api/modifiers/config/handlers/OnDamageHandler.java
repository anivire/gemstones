package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    Map<EventType, List<GemstoneModifier>> types = modifiers.stream()
        .collect(Collectors.groupingBy(m -> ((OnDamageConfig) m.getConfig()).eventType()));

    types.forEach((type, group) -> {
      switch (type) {
        default -> {
        }
      }
    });
  }
}
