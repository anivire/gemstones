package name.modid.core.content.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public class EffectsRegistry {
  private static final DeferredRegister<StatusEffect> EFFECTS = DeferredRegister.create(Gemstones.MOD_ID,
      RegistryKeys.STATUS_EFFECT);

  public static final RegistrySupplier<StatusEffect> BLEEDING = EFFECTS.register("bleeding", BleedingEffect::new);

  public static final RegistrySupplier<StatusEffect> GUARDIAN_SMITE = EFFECTS.register("guardian_smite",
      GuardianSmiteEffect::new);

  public static final RegistrySupplier<StatusEffect> HARVEST_MARK = EFFECTS.register("harvest_mark",
      HarvestMarkEffect::new);

  public static final RegistrySupplier<StatusEffect> STUNNED = EFFECTS.register("stunned", StunnedEffect::new);

  public static final RegistrySupplier<StatusEffect> DETONATE = EFFECTS.register("detonate", DetonateEffect::new);

  public static final RegistrySupplier<StatusEffect> FREEZING = EFFECTS.register("freezing", FreezingEffect::new);

  public static final RegistrySupplier<StatusEffect> LOOT_TOUCH = EFFECTS.register("loot_touch", LootTouchEffect::new);

  public static final RegistrySupplier<StatusEffect> SCARAB = EFFECTS.register("scarab", ScarabEffect::new);

  public static final RegistrySupplier<StatusEffect> SOUL_BURN = EFFECTS.register("soul_burn", SoulBurnEffect::new);

  public static final RegistrySupplier<StatusEffect> RADIANCE = EFFECTS.register("radiance", RadianceEffect::new);

  public static final RegistrySupplier<StatusEffect> LETHAL_WEAKNESS = EFFECTS.register("lethal_weakness",
      LethalWeaknessEffect::new);

  public static void initialize() {
    Gemstones.LOGGER.info("Registering mod effects for {}", Gemstones.MOD_ID);
    EFFECTS.register();
  }

  private static RegistryEntry<StatusEffect> entry(RegistrySupplier<StatusEffect> supplier) {
    return Registries.STATUS_EFFECT.getEntry(supplier.get());
  }

  public static RegistryEntry<StatusEffect> bleedingEntry() {
    return entry(BLEEDING);
  }

  public static RegistryEntry<StatusEffect> guardianSmiteEntry() {
    return entry(GUARDIAN_SMITE);
  }

  public static RegistryEntry<StatusEffect> harvestMarkEntry() {
    return entry(HARVEST_MARK);
  }

  public static RegistryEntry<StatusEffect> stunnedEntry() {
    return entry(STUNNED);
  }

  public static RegistryEntry<StatusEffect> detonateEntry() {
    return entry(DETONATE);
  }

  public static RegistryEntry<StatusEffect> freezingEntry() {
    return entry(FREEZING);
  }

  public static RegistryEntry<StatusEffect> lootTouchEntry() {
    return entry(LOOT_TOUCH);
  }

  public static RegistryEntry<StatusEffect> scarabEntry() {
    return entry(SCARAB);
  }

  public static RegistryEntry<StatusEffect> soulBurnEntry() {
    return entry(SOUL_BURN);
  }

  public static RegistryEntry<StatusEffect> radianceEntry() {
    return entry(RADIANCE);
  }

  public static RegistryEntry<StatusEffect> lethalWeaknessEntry() {
    return entry(LETHAL_WEAKNESS);
  }
}