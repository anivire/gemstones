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
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.items.registries.ItemsRegistry;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;

public class OnFishingHandler implements ModifierHandler<ModifierConfig.OnFishingConfig> {
  private final Map<String, java.util.function.BiConsumer<List<GemstoneModifier>, ModifierContext>> handlers = Map.of(
      "ON_FISHING_INCREASE_MOSSY_BOX_DROP", this::handleMossyBoxDrop);

  private static final List<String> ORDER = List.of(
      "ON_FISHING_INCREASE_MOSSY_BOX_DROP");

  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty()) {
      return;
    }

    Map<String, List<GemstoneModifier>> grouped = modifiers.stream()
        .collect(Collectors.groupingBy(inst -> ((OnFishingConfig) inst.getConfig()).eventType().getName()));

    for (String key : ORDER) {
      var group = grouped.get(key);
      if (group == null || group.isEmpty())
        continue;

      var fn = handlers.get(key);
      if (fn != null)
        fn.accept(group, ctx);
    }
  }

  private void handleMossyBoxDrop(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity owner)
        || !(ctx.getTarget() instanceof FishingBobberEntity target)) {
      return;
    }

    double chance = ModifierUtils.cappedProcChance(
        modifiers.stream()
            .map(m -> ((OnFishingConfig) m.getConfig()).values().get(m.getRarityType()))
            .toList());

    if (ModifierUtils.proc(ctx.getWorld(), chance)) {
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