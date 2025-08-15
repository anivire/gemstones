package name.modid.helpers.modifiers.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import name.modid.effects.EffectRegistrationHelper;
import name.modid.helpers.attributes.AttributeRegistrationHelper;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.ModifierData;
import name.modid.helpers.modifiers.ModifierItemCaregory;
import name.modid.helpers.modifiers.modifierTypes.ModifierAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierMultiplyAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffectProjectile;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;

public record TopazModifierData() implements ModifierData {
  private static final Map<ModifierItemCaregory, GemstoneModifier> MODIFIERS = new HashMap<>();

  static {
    MODIFIERS.put(ModifierItemCaregory.MELEE, new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
        new ArrayList<Double>(Arrays.asList(0.25, 0.35, 0.50, 0.65)), ModifierItemCaregory.MELEE,
        AttributeRegistrationHelper.CRIT_DAMAGE_ATTRIBUTE, GemstoneType.TOPAZ));

    MODIFIERS.put(ModifierItemCaregory.RANGED,
        new ModifierOnHitEffectProjectile(
            new ArrayList<Double>(Arrays.asList(0.1, 0.2, 0.35, 0.45)), 11, 0,
            ModifierItemCaregory.RANGED, StatusEffects.SLOWNESS, true, 3, GemstoneType.TOPAZ));

    MODIFIERS.put(ModifierItemCaregory.TOOLS,
        new ModifierOnHitEffect(new ArrayList<Double>(Arrays.asList(0.1, 0.2, 0.3, 0.4)), 6, 0,
            ModifierItemCaregory.TOOLS, EffectRegistrationHelper.HARVEST_MARK_EFFECT, true, 10,
            GemstoneType.TOPAZ));

    MODIFIERS.put(ModifierItemCaregory.ARMOR,
        new ModifierMultiplyAttribute(new ArrayList<ModifierAttribute>(Arrays.asList(
            new ModifierAttribute(Operation.ADD_VALUE,
                new ArrayList<Double>(Arrays.asList(0.5, 1.0, 1.5, 2.0)),
                ModifierItemCaregory.ARMOR, EntityAttributes.GENERIC_ARMOR, GemstoneType.TOPAZ),
            new ModifierAttribute(Operation.ADD_VALUE,
                new ArrayList<Double>(Arrays.asList(0.5, 0.5, 1.0, 1.5)),
                ModifierItemCaregory.ARMOR, EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
                GemstoneType.TOPAZ)))));
  }

  @Override
  public Map<ModifierItemCaregory, GemstoneModifier> getModifiers() {
    return new HashMap<>(MODIFIERS);
  }
}
