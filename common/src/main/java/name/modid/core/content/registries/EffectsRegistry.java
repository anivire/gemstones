package name.modid.core.content.registries;

import name.modid.Gemstones;
import name.modid.core.content.effects.BleedingEffect;
import name.modid.core.content.effects.DetonateEffect;
import name.modid.core.content.effects.FreezingEffect;
import name.modid.core.content.effects.GuardianSmiteEffect;
import name.modid.core.content.effects.HarvestMarkEffect;
import name.modid.core.content.effects.LethalWeaknessEffect;
import name.modid.core.content.effects.LootTouchEffect;
import name.modid.core.content.effects.RadianceEffect;
import name.modid.core.content.effects.ScarabEffect;
import name.modid.core.content.effects.SoulBurnEffect;
import name.modid.core.content.effects.StunnedEffect;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKeys;

public class EffectsRegistry {
  private static final DeferredRegister<StatusEffect> EFFECTS = DeferredRegister.create(Gemstones.MOD_ID,
      RegistryKeys.STATUS_EFFECT);

  public static final RegistryEntry<StatusEffect> BLEEDING_EFFECT = EFFECTS.register("bleeding", BleedingEffect::new);

  public static final RegistryEntry<StatusEffect> GUARDIAN_SMITE_EFFECT = EFFECTS.register(
      "guardian_smite",
      GuardianSmiteEffect::new);

  public static final RegistryEntry<StatusEffect> HARVEST_MARK_EFFECT = EFFECTS.register(
      "harvest_mark",
      HarvestMarkEffect::new);

  public static final RegistryEntry<StatusEffect> STUNNED_EFFECT = EFFECTS.register("stunned", StunnedEffect::new);

  public static final RegistryEntry<StatusEffect> DETONATE_EFFECT = EFFECTS.register("detonate", DetonateEffect::new);

  public static final RegistryEntry<StatusEffect> FREEZING_EFFECT = EFFECTS.register("freezing", FreezingEffect::new);

  public static final RegistryEntry<StatusEffect> LOOT_TOUCH_EFFECT = EFFECTS.register(
      "loot_touch",
      LootTouchEffect::new);

  public static final RegistryEntry<StatusEffect> SCARAB_EFFECT = EFFECTS.register("scarab", ScarabEffect::new);

  public static final RegistryEntry<StatusEffect> SOUL_BURN_EFFECT = EFFECTS.register("soul_burn", SoulBurnEffect::new);

  public static final RegistryEntry<StatusEffect> RADIANCE_EFFECT = EFFECTS.register("radiance", RadianceEffect::new);

  public static final RegistryEntry<StatusEffect> LETHAL_WEAKNESS_EFFECT = EFFECTS.register(
      "lethal_weakness",
      LethalWeaknessEffect::new);

  public static void initialize() {
    Gemstones.LOGGER.info("Registering mod effects for {}", Gemstones.MOD_ID);
    EFFECTS.register();
  }
}
