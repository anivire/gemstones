package name.modid.core.content.registries;

import name.modid.Gemstones;
import name.modid.core.content.effects.AmberBlessingEffect;
import name.modid.core.content.effects.BleedingEffect;
import name.modid.core.content.effects.DetonateEffect;
import name.modid.core.content.effects.ExperienceThirstEffect;
import name.modid.core.content.effects.FreezingEffect;
import name.modid.core.content.effects.GuardianSmiteEffect;
import name.modid.core.content.effects.HarvestMarkEffect;
import name.modid.core.content.effects.LootTouchEffect;
import name.modid.core.content.effects.PlagueEffect;
import name.modid.core.content.effects.QuickSandsEffect;
import name.modid.core.content.effects.ScarabEffect;
import name.modid.core.content.effects.SoulBurnEffect;
import name.modid.core.content.effects.StunnedEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class EffectsRegistry {
  public static final RegistryEntry<StatusEffect> EXP_THIRST_EFFECT = Registry.registerReference(
      Registries.STATUS_EFFECT,
      Identifier.of(Gemstones.MOD_ID, "exp_thirst"), new ExperienceThirstEffect());

  public static final RegistryEntry<StatusEffect> BLEEDING_EFFECT = Registry.registerReference(
      Registries.STATUS_EFFECT, Identifier.of(Gemstones.MOD_ID, "bleeding"), new BleedingEffect());

  public static final RegistryEntry<StatusEffect> GUARDIAN_SMITE_EFFECT = Registry.registerReference(
      Registries.STATUS_EFFECT,
      Identifier.of(Gemstones.MOD_ID, "guardian_smite"), new GuardianSmiteEffect());

  public static final RegistryEntry<StatusEffect> QUICK_SANDS_EFFECT = Registry.registerReference(
      Registries.STATUS_EFFECT,
      Identifier.of(Gemstones.MOD_ID, "quick_sands"), new QuickSandsEffect());

  public static final RegistryEntry<StatusEffect> HARVEST_MARK_EFFECT = Registry.registerReference(
      Registries.STATUS_EFFECT,
      Identifier.of(Gemstones.MOD_ID, "harvest_mark"), new HarvestMarkEffect());

  public static final RegistryEntry<StatusEffect> STUNNED_EFFECT = Registry.registerReference(
      Registries.STATUS_EFFECT, Identifier.of(Gemstones.MOD_ID, "stunned"), new StunnedEffect());

  public static final RegistryEntry<StatusEffect> DETONATE_EFFECT = Registry.registerReference(
      Registries.STATUS_EFFECT, Identifier.of(Gemstones.MOD_ID, "detonate"), new DetonateEffect());

  public static final RegistryEntry<StatusEffect> PLAGUE_EFFECT = Registry.registerReference(
      Registries.STATUS_EFFECT, Identifier.of(Gemstones.MOD_ID, "plague"), new PlagueEffect());

  public static final RegistryEntry<StatusEffect> FREEZING_EFFECT = Registry.registerReference(
      Registries.STATUS_EFFECT, Identifier.of(Gemstones.MOD_ID, "freezing"), new FreezingEffect());

  public static final RegistryEntry<StatusEffect> LOOT_TOUCH_EFFECT = Registry.registerReference(
      Registries.STATUS_EFFECT, Identifier.of(Gemstones.MOD_ID, "loot_touch"), new LootTouchEffect());

  public static final RegistryEntry<StatusEffect> SCARAB_EFFECT = Registry.registerReference(
      Registries.STATUS_EFFECT, Identifier.of(Gemstones.MOD_ID, "scarab"), new ScarabEffect());

  public static final RegistryEntry<StatusEffect> AMBER_BLESSING_EFFECT = Registry.registerReference(
      Registries.STATUS_EFFECT, Identifier.of(Gemstones.MOD_ID, "amber_blessing"), new AmberBlessingEffect());

  public static final RegistryEntry<StatusEffect> SOUL_BURN_EFFECT = Registry.registerReference(
      Registries.STATUS_EFFECT, Identifier.of(Gemstones.MOD_ID, "soul_burn"), new SoulBurnEffect());

  public static void initialize() {
    Gemstones.LOGGER.info("Registering mod effects for {}", Gemstones.MOD_ID);
  }
}
