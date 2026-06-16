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
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public class EffectsRegistry {
  private static final DeferredRegister<StatusEffect> EFFECTS = DeferredRegister.create(Gemstones.MOD_ID,
      RegistryKeys.STATUS_EFFECT);

  private static final RegistrySupplier<StatusEffect> BLEEDING_SUPPLIER = EFFECTS.register("bleeding",
      BleedingEffect::new);

  private static final RegistrySupplier<StatusEffect> GUARDIAN_SMITE_SUPPLIER = EFFECTS.register(
      "guardian_smite",
      GuardianSmiteEffect::new);

  private static final RegistrySupplier<StatusEffect> HARVEST_MARK_SUPPLIER = EFFECTS.register(
      "harvest_mark",
      HarvestMarkEffect::new);

  private static final RegistrySupplier<StatusEffect> STUNNED_SUPPLIER = EFFECTS.register("stunned",
      StunnedEffect::new);

  private static final RegistrySupplier<StatusEffect> DETONATE_SUPPLIER = EFFECTS.register("detonate",
      DetonateEffect::new);

  private static final RegistrySupplier<StatusEffect> FREEZING_SUPPLIER = EFFECTS.register("freezing",
      FreezingEffect::new);

  private static final RegistrySupplier<StatusEffect> LOOT_TOUCH_SUPPLIER = EFFECTS.register(
      "loot_touch",
      LootTouchEffect::new);

  private static final RegistrySupplier<StatusEffect> SCARAB_SUPPLIER = EFFECTS.register("scarab",
      ScarabEffect::new);

  private static final RegistrySupplier<StatusEffect> SOUL_BURN_SUPPLIER = EFFECTS.register("soul_burn",
      SoulBurnEffect::new);

  private static final RegistrySupplier<StatusEffect> RADIANCE_SUPPLIER = EFFECTS.register("radiance",
      RadianceEffect::new);

  private static final RegistrySupplier<StatusEffect> LETHAL_WEAKNESS_SUPPLIER = EFFECTS.register(
      "lethal_weakness",
      LethalWeaknessEffect::new);

  public static RegistryEntry<StatusEffect> BLEEDING_EFFECT;
  public static RegistryEntry<StatusEffect> GUARDIAN_SMITE_EFFECT;
  public static RegistryEntry<StatusEffect> HARVEST_MARK_EFFECT;
  public static RegistryEntry<StatusEffect> STUNNED_EFFECT;
  public static RegistryEntry<StatusEffect> DETONATE_EFFECT;
  public static RegistryEntry<StatusEffect> FREEZING_EFFECT;
  public static RegistryEntry<StatusEffect> LOOT_TOUCH_EFFECT;
  public static RegistryEntry<StatusEffect> SCARAB_EFFECT;
  public static RegistryEntry<StatusEffect> SOUL_BURN_EFFECT;
  public static RegistryEntry<StatusEffect> RADIANCE_EFFECT;
  public static RegistryEntry<StatusEffect> LETHAL_WEAKNESS_EFFECT;

  public static void initialize() {
    Gemstones.LOGGER.info("Registering mod effects for {}", Gemstones.MOD_ID);
    EFFECTS.register();

    BLEEDING_EFFECT = BLEEDING_SUPPLIER;
    GUARDIAN_SMITE_EFFECT = GUARDIAN_SMITE_SUPPLIER;
    HARVEST_MARK_EFFECT = HARVEST_MARK_SUPPLIER;
    STUNNED_EFFECT = STUNNED_SUPPLIER;
    DETONATE_EFFECT = DETONATE_SUPPLIER;
    FREEZING_EFFECT = FREEZING_SUPPLIER;
    LOOT_TOUCH_EFFECT = LOOT_TOUCH_SUPPLIER;
    SCARAB_EFFECT = SCARAB_SUPPLIER;
    SOUL_BURN_EFFECT = SOUL_BURN_SUPPLIER;
    RADIANCE_EFFECT = RADIANCE_SUPPLIER;
    LETHAL_WEAKNESS_EFFECT = LETHAL_WEAKNESS_SUPPLIER;
  }
}
