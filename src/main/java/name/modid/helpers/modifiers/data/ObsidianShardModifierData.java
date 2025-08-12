package name.modid.helpers.modifiers.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import name.modid.entities.EffectRegistrationHelper;
import name.modid.helpers.attributes.AttributeRegistrationHelper;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.GemstoneModifierItemType;
import name.modid.helpers.modifiers.GemstonesModifierData;
import name.modid.helpers.modifiers.modifierTypes.ModifierAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffectProjectile;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;

public record ObsidianShardModifierData() implements GemstonesModifierData {
  private static final Map<GemstoneModifierItemType, GemstoneModifier> MODIFIERS = new HashMap<>();

  static {
    MODIFIERS.put(GemstoneModifierItemType.MELEE,
        new ModifierOnHitEffect(new ArrayList<Double>(Arrays.asList(0.08, 0.15, 0.25, 0.3)), 2, 0,
            GemstoneModifierItemType.MELEE, EffectRegistrationHelper.STUNNED_EFFECT, false, 0,
            GemstoneType.OBSIDIAN_SHARD));

    MODIFIERS.put(GemstoneModifierItemType.RANGED,
        new ModifierOnHitEffectProjectile(
            new ArrayList<Double>(Arrays.asList(0.16, 0.25, 0.35, 0.45)), 2, 0,
            GemstoneModifierItemType.RANGED, EffectRegistrationHelper.STUNNED_EFFECT, false, 0,
            GemstoneType.OBSIDIAN_SHARD));

    MODIFIERS.put(GemstoneModifierItemType.TOOLS,
        new ModifierAttribute(Operation.ADD_VALUE,
            new ArrayList<Double>(Arrays.asList(125.0, 255.0, 350.0, 500.0)),
            GemstoneModifierItemType.TOOLS, AttributeRegistrationHelper.MAX_DURABILITY_ATTRIBUTE,
            GemstoneType.OBSIDIAN_SHARD));

    // MODIFIERS.put(GemstoneModifierItemType.ARMOR,
    // new ModifierMultiplyAttribute(new ArrayList<ModifierAttribute>(Arrays.asList(
    // new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
    // new ArrayList<Double>(Arrays.asList(0.01, 0.03, 0.05, 0.09)),
    // GemstoneModifierItemType.ARMOR, EntityAttributes.GENERIC_ARMOR,
    // GemstoneType.OBSIDIAN_SHARD),
    // new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
    // new ArrayList<Double>(Arrays.asList(-0.03, -0.05, -0.08, -0.12)),
    // GemstoneModifierItemType.ARMOR, EntityAttributes.GENERIC_MOVEMENT_SPEED,
    // GemstoneType.OBSIDIAN_SHARD)))));

    MODIFIERS.put(GemstoneModifierItemType.ARMOR,
        new ModifierAttribute(Operation.ADD_VALUE,
            new ArrayList<Double>(Arrays.asList(95.0, 155.0, 205.0, 300.0)),
            GemstoneModifierItemType.ARMOR, AttributeRegistrationHelper.MAX_DURABILITY_ATTRIBUTE,
            GemstoneType.OBSIDIAN_SHARD));
  }

  @Override
  public Map<GemstoneModifierItemType, GemstoneModifier> getModifiers() {
    return new HashMap<>(MODIFIERS);
  }
}
