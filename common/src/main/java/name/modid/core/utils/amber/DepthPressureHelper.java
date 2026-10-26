package name.modid.core.utils.amber;

import java.util.List;

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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

// Amber tools: mining speed grows with depth, from nothing at sea level to the full gemstone
// value at the bottom of the world
public final class DepthPressureHelper {
  private static final Identifier PRESSURE_ID = Identifier.of(Gemstones.MOD_ID, "depth_pressure");

  private DepthPressureHelper() {
  }

  public static void tick(ServerPlayerEntity player) {
    EntityAttributeInstance breakSpeed = player.getAttributeInstance(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED);
    if (breakSpeed == null) {
      return;
    }

    double bonus = getMaxBonus(player) * getDepthFactor(player.getWorld(), player.getBlockY());
    EntityAttributeModifier current = breakSpeed.getModifier(PRESSURE_ID);

    if (bonus <= 0.0) {
      if (current != null) {
        breakSpeed.removeModifier(PRESSURE_ID);
      }
      return;
    }

    if (current != null && current.value() == bonus) {
      return;
    }

    breakSpeed.removeModifier(PRESSURE_ID);
    breakSpeed.addTemporaryModifier(new EntityAttributeModifier(
        PRESSURE_ID, bonus, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
  }

  private static double getDepthFactor(World world, int y) {
    int seaLevel = world.getSeaLevel();
    int bottom = world.getBottomY();
    if (seaLevel <= bottom) {
      return 0.0;
    }

    return MathHelper.clamp((double) (seaLevel - y) / (seaLevel - bottom), 0.0, 1.0);
  }

  private static double getMaxBonus(ServerPlayerEntity player) {
    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(player,
        PlayerConfig.class);
    double bonus = 0.0;

    for (GemstoneModifier modifier : modifiers) {
      if (modifier.getItemCategory() == ModifierItemCategory.TOOLS
          && modifier.getConfig() instanceof PlayerConfig config
          && config.eventType() == EventType.PLAYER_TICK_DEPTH_PRESSURE) {
        bonus += config.values().get(modifier.getRarityType());
      }
    }

    return bonus;
  }
}
