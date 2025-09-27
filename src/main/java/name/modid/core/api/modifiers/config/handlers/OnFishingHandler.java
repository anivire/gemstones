package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnFishingConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnPotionBrewConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.content.items.registries.ItemsRegistry;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;

public class OnFishingHandler
    implements ModifierHandler<ModifierConfig.OnFishingConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    EventType type = ((OnFishingConfig) modifiers.get(0).getConfig()).eventType();

    switch (type) {
      case ON_FISHING_INCREASE_MOSSY_BOX_DROP -> handleMossyBoxDrop(modifiers, ctx);
      default -> {
      }
    }
  }

  private void handleMossyBoxDrop(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (ctx.getInventory() == null) {
      return;
    }

    double totalIncreasedChanceValue = 0.0;
    for (GemstoneModifier modifier : modifiers) {
      OnPotionBrewConfig config = (OnPotionBrewConfig) modifier.getConfig();
      totalIncreasedChanceValue += config.values().get(modifier.getRarityType());
    }

    if (ctx.getOwner() instanceof LivingEntity owner
        && ctx.getTarget() instanceof FishingBobberEntity target
        && ctx.getWorld().random.nextFloat() < totalIncreasedChanceValue) {
      ItemStack specialDrop = new ItemStack(ItemsRegistry.MOSSY_BOX);
      ItemEntity itemEntity = new ItemEntity(
          ctx.getWorld(),
          target.getX(),
          target.getY(),
          target.getZ(),
          specialDrop);

      double dx = owner.getX() - target.getX();
      double dy = owner.getY() - target.getY();
      double dz = owner.getZ() - target.getZ();

      itemEntity.setVelocity(
          dx * 0.1,
          dy * 0.1 + Math.sqrt(Math.sqrt(dx * dx + dy * dy + dz * dz)) * 0.08,
          dz * 0.1);
      target.getWorld().spawnEntity(itemEntity);
      owner.getWorld().spawnEntity(new ExperienceOrbEntity(
          owner.getWorld(),
          owner.getX(),
          owner.getY() + 0.5,
          owner.getZ() + 0.5,
          target.getRandom().nextInt(6) + 1));
    }
  }
}