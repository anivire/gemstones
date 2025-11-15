package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnFishingConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.config.ModifierUtils;
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

    Map<EventType, List<GemstoneModifier>> types = modifiers.stream()
        .collect(Collectors.groupingBy(m -> ((OnFishingConfig) m.getConfig()).eventType()));

    types.forEach((type, group) -> {
      switch (type) {
        case ON_FISHING_INCREASE_MOSSY_BOX_DROP -> handleMossyBoxDrop(group, ctx);
        default -> {
        }
      }
    });
  }

  private void handleMossyBoxDrop(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    // if (ctx.getInventory() == null) {
    // return;
    // }

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      OnFishingConfig config = (OnFishingConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (ctx.getOwner() instanceof LivingEntity owner
        && ctx.getTarget() instanceof FishingBobberEntity target
        && ctx.getWorld().random.nextFloat() < (float) combinedChance) {
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