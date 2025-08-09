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
import name.modid.helpers.modifiers.modifierTypes.ModifierMultiplyAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffectProjectile;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;

public record TopazModifierData() implements GemstonesModifierData {
  private static final Map<GemstoneModifierItemType, GemstoneModifier> MODIFIERS = new HashMap<>();

  static {
    MODIFIERS.put(GemstoneModifierItemType.MELEE,
        new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
            new ArrayList<Double>(Arrays.asList(0.25, 0.35, 0.50, 0.65)),
            GemstoneModifierItemType.MELEE, AttributeRegistrationHelper.CRIT_DAMAGE_ATTRIBUTE,
            GemstoneType.TOPAZ));

    MODIFIERS.put(GemstoneModifierItemType.RANGED,
        new ModifierOnHitEffectProjectile(
            new ArrayList<Double>(Arrays.asList(0.1, 0.2, 0.35, 0.45)), 11, 0,
            GemstoneModifierItemType.RANGED, StatusEffects.SLOWNESS, true, 3, GemstoneType.TOPAZ));

    MODIFIERS.put(GemstoneModifierItemType.TOOLS,
        new ModifierOnHitEffect(new ArrayList<Double>(Arrays.asList(0.1, 0.2, 0.3, 0.4)), 6, 0,
            GemstoneModifierItemType.TOOLS, EffectRegistrationHelper.HARVEST_MARK_EFFECT, true, 10,
            GemstoneType.TOPAZ));

    MODIFIERS.put(GemstoneModifierItemType.ARMOR,
        new ModifierMultiplyAttribute(new ArrayList<ModifierAttribute>(Arrays.asList(
            new ModifierAttribute(Operation.ADD_VALUE,
                new ArrayList<Double>(Arrays.asList(0.5, 1.0, 1.5, 2.0)),
                GemstoneModifierItemType.ARMOR, EntityAttributes.GENERIC_ARMOR, GemstoneType.TOPAZ),
            new ModifierAttribute(Operation.ADD_VALUE,
                new ArrayList<Double>(Arrays.asList(0.5, 0.5, 1.0, 1.5)),
                GemstoneModifierItemType.ARMOR, EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
                GemstoneType.TOPAZ)))));
  }

  @Override
  public Map<GemstoneModifierItemType, GemstoneModifier> getModifiers() {
    return new HashMap<>(MODIFIERS);
  }
}
