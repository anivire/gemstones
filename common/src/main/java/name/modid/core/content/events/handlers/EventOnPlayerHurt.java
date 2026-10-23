package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectMeleeConfig;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import name.modid.core.content.effects.StunnedEffect;
import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

// On-hit effects placed in armor are applied to the attacker when it hits the player in melee
// (Amber armor: "Hardened Resin" sticks Scarab to the attacker)
public class EventOnPlayerHurt {
  public static void setup(LivingEntity target, DamageSource source, float amount) {
    if (!(target instanceof ServerPlayerEntity player)
        || !(player.getWorld() instanceof ServerWorld serverWorld)
        || !(source.getAttacker() instanceof LivingEntity attacker)
        || attacker == player
        || source.getSource() != attacker) {
      return;
    }

    List<GemstoneModifier> modifiers = new ArrayList<>(ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        player, HitEffectMeleeConfig.class));
    modifiers.removeIf(modifier -> modifier.getItemCategory() != ModifierItemCategory.ARMOR);

    if (modifiers.isEmpty()) {
      return;
    }

    Map<RegistryEntry<StatusEffect>, List<GemstoneModifier>> groupedByEffect = modifiers.stream()
        .collect(Collectors.groupingBy(mod -> ((HitEffectMeleeConfig) mod.getConfig()).effect()));

    for (Map.Entry<RegistryEntry<StatusEffect>, List<GemstoneModifier>> entry : groupedByEffect.entrySet()) {
      RegistryEntry<StatusEffect> effect = entry.getKey();
      if (effect.equals(EffectsRegistry.stunnedEntry()) && StunnedEffect.isImmuneToStun(attacker)) {
        continue;
      }

      List<Double> chances = new ArrayList<>();
      int amplifier = 0;
      int duration = 0;

      for (GemstoneModifier modifier : entry.getValue()) {
        HitEffectMeleeConfig config = (HitEffectMeleeConfig) modifier.getConfig();
        chances.add(config.chance().get(modifier.getRarityType()));
        amplifier = Math.max(amplifier, config.amplifier());
        duration = Math.max(duration, config.duration());
      }

      if (ModifierUtils.proc(serverWorld, ModifierUtils.cappedProcChance(chances))) {
        attacker.addStatusEffect(new StatusEffectInstance(effect, duration * 20, amplifier));
      }
    }
  }
}
