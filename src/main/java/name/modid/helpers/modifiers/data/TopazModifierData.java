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
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;

public record TopazModifierData() implements GemstonesModifierData {
  private static final Map<GemstoneModifierItemType, GemstoneModifier> MODIFIERS = new HashMap<>();

  static {
    MODIFIERS.put(GemstoneModifierItemType.MELEE,
        new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
            new ArrayList<Double>(Arrays.asList(0.25, 0.35, 0.50, 0.65)),
            GemstoneModifierItemType.MELEE, AttributeRegistrationHelper.CRIT_DAMAGE_ATTRIBUTE,
            GemstoneType.TOPAZ));

    MODIFIERS.put(GemstoneModifierItemType.RANGED,
        new ModifierOnHitEffect(new ArrayList<Double>(Arrays.asList(0.1, 0.2, 0.3, 0.4)), 6, 0,
            GemstoneModifierItemType.MELEE, EffectRegistrationHelper.QUICK_SANDS_EFFECT, true, 5,
            GemstoneType.RUBY));

    MODIFIERS.put(GemstoneModifierItemType.TOOLS,
        new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
            new ArrayList<Double>(Arrays.asList(0.5, 1.0, 1.5, 2.0)),
            GemstoneModifierItemType.TOOLS, EntityAttributes.PLAYER_BLOCK_BREAK_SPEED,
            GemstoneType.TOPAZ));

    MODIFIERS.put(GemstoneModifierItemType.ARMOR,
        new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
            new ArrayList<Double>(Arrays.asList(0.05, 0.10, 0.15, 0.25)),
            GemstoneModifierItemType.ARMOR, EntityAttributes.GENERIC_LUCK, GemstoneType.TOPAZ));
  }

  @Override
  public Map<GemstoneModifierItemType, GemstoneModifier> getModifiers() {
    return new HashMap<>(MODIFIERS);
  }
}
