package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnPlayerDamageConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

public class OnPlayerDamageHandler implements ModifierHandler<ModifierConfig.OnPlayerDamageConfig> {
  private final Map<String, java.util.function.BiConsumer<List<GemstoneModifier>, ModifierContext>> handlers = Map.of(
      "PLAYER_SAVE_LETHAL", this::handleSaveLethal);

  private static final List<String> ORDER = List.of(
      "PLAYER_SAVE_LETHAL");

  @Override
  public boolean supports(GemstoneModifier modifier) {
    return modifier.getItemCategory() == ModifierItemCategory.ARMOR;
  }

  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty()) {
      return;
    }

    Map<String, List<GemstoneModifier>> grouped = modifiers.stream()
        .collect(Collectors.groupingBy(inst -> ((OnPlayerDamageConfig) inst.getConfig()).eventType().getName()));

    for (String key : ORDER) {
      var group = grouped.get(key);
      if (group == null || group.isEmpty())
        continue;

      var fn = handlers.get(key);
      if (fn != null)
        fn.accept(group, ctx);
    }
  }

  private void handleSaveLethal(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity owner)
        || owner.isRemoved()
        || owner.isSpectator()
        || owner.hasStatusEffect(EffectsRegistry.lethalWeaknessEntry())) {
      return;
    }

    if (!(owner.getWorld() instanceof ServerWorld server)) {
      return;
    }

    int amplifier = 1;
    int duration = 0;
    double healthThreshold = 0.0;

    for (GemstoneModifier modifier : modifiers) {
      OnPlayerDamageConfig config = (OnPlayerDamageConfig) modifier.getConfig();

      duration += config.values().get(modifier.getRarityType());
      healthThreshold = Math.max(
          healthThreshold,
          config.additionalValues().get(modifier.getRarityType()));
    }

    int buffDurationTicks = Math.max(0, duration) * 20;

    if (buffDurationTicks <= 0) {
      return;
    }

    // Так как этот handler теперь вызывается из LIVING_DEATH,
    // значит урон уже летальный. Спасаем как тотем.
    owner.setHealth(Math.max(1.0F, (float) healthThreshold));
    ctx.setIsHurtable(false);

    server.playSound(
        null,
        owner.getBlockPos(),
        net.minecraft.sound.SoundEvents.ITEM_TOTEM_USE,
        net.minecraft.sound.SoundCategory.PLAYERS,
        1.0f,
        1.0f);

    owner.addStatusEffect(new StatusEffectInstance(
        StatusEffects.REGENERATION,
        buffDurationTicks,
        amplifier,
        false,
        true,
        true));

    owner.addStatusEffect(new StatusEffectInstance(
        StatusEffects.RESISTANCE,
        buffDurationTicks,
        0,
        false,
        true,
        true));

    owner.addStatusEffect(new StatusEffectInstance(
        EffectsRegistry.lethalWeaknessEntry(),
        5 * 60 * 20,
        0,
        false,
        true,
        true));

    owner.addStatusEffect(new StatusEffectInstance(
        StatusEffects.ABSORPTION,
        buffDurationTicks,
        1,
        false,
        true,
        true));

    double x = owner.getX();
    double y = owner.getBodyY(0.5);
    double z = owner.getZ();

    server.spawnParticles(
        ParticleTypes.TOTEM_OF_UNDYING,
        x, y, z,
        50,
        0.5, 0.8, 0.5,
        0.1);

    server.spawnParticles(
        ParticleTypes.GLOW,
        x, y, z,
        20,
        0.3, 0.6, 0.3,
        0.02);
  }
}
