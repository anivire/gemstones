package name.modid.core.utils.amber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.PlayerConfig;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

// Amber armor: after 25 seconds without taking damage the player gets extra max health,
// which breaks on the next hit
public final class AmberShellHelper {
  public static final int RECHARGE_DELAY_SECONDS = 25;
  private static final long RECHARGE_DELAY_TICKS = RECHARGE_DELAY_SECONDS * 20;
  private static final Identifier SHELL_ID = Identifier.of(Gemstones.MOD_ID, "amber_shell");
  private static final Map<UUID, Long> LAST_HURT_TIMES = new HashMap<>();

  private AmberShellHelper() {
  }

  public static void onHurt(ServerPlayerEntity player) {
    LAST_HURT_TIMES.put(player.getUuid(), player.getWorld().getTime());
    breakShell(player);
  }

  public static void tick(ServerPlayerEntity player) {
    EntityAttributeInstance maxHealth = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
    if (maxHealth == null) {
      return;
    }

    double shell = getShellAmount(player);
    EntityAttributeModifier current = maxHealth.getModifier(SHELL_ID);

    if (shell <= 0.0) {
      if (current != null) {
        // Taking the armor off counts as a hit, otherwise swapping it would heal for free
        LAST_HURT_TIMES.put(player.getUuid(), player.getWorld().getTime());
        breakShell(player);
      }
      return;
    }

    long lastHurt = LAST_HURT_TIMES.getOrDefault(player.getUuid(), Long.MIN_VALUE / 2);
    if (player.getWorld().getTime() - lastHurt < RECHARGE_DELAY_TICKS) {
      return;
    }

    if (current == null) {
      maxHealth.addTemporaryModifier(new EntityAttributeModifier(
          SHELL_ID, shell, EntityAttributeModifier.Operation.ADD_VALUE));
      // The new hearts come filled, so the shell works as a buffer right away
      player.heal((float) shell);
    } else if (current.value() != shell) {
      maxHealth.removeModifier(SHELL_ID);
      maxHealth.addTemporaryModifier(new EntityAttributeModifier(
          SHELL_ID, shell, EntityAttributeModifier.Operation.ADD_VALUE));
      clampHealth(player);
    }
  }

  private static void breakShell(ServerPlayerEntity player) {
    EntityAttributeInstance maxHealth = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
    if (maxHealth != null && maxHealth.getModifier(SHELL_ID) != null) {
      maxHealth.removeModifier(SHELL_ID);
      clampHealth(player);
    }
  }

  private static void clampHealth(ServerPlayerEntity player) {
    if (player.getHealth() > player.getMaxHealth()) {
      player.setHealth(player.getMaxHealth());
    }
  }

  private static double getShellAmount(ServerPlayerEntity player) {
    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(player,
        PlayerConfig.class);
    double shell = 0.0;

    for (GemstoneModifier modifier : modifiers) {
      if (modifier.getItemCategory() == ModifierItemCategory.ARMOR
          && modifier.getConfig() instanceof PlayerConfig config
          && config.eventType() == EventType.PLAYER_TICK_AMBER_SHELL) {
        shell += config.values().get(modifier.getRarityType());
      }
    }

    return shell;
  }
}
