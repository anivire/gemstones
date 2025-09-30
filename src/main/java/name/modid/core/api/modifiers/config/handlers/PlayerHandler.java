package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.PlayerConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.types.EventType;

public class PlayerHandler implements ModifierHandler<ModifierConfig.PlayerConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    EventType type = ((PlayerConfig) modifiers.get(0).getConfig()).eventType();

    switch (type) {
      case PLAYER_MULTIPLY_DAMAGE_ARMORLESS -> multiplyDamageArmorless(modifiers, ctx);
      default -> {
      }
    }
  }

  private void multiplyDamageArmorless(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {

  }
}
