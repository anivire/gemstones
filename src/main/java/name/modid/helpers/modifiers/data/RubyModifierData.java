package name.modid.helpers.modifiers.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import name.modid.entities.EffectRegistrationHelper;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.GemstoneModifierItemType;
import name.modid.helpers.modifiers.GemstonesModifierData;
import name.modid.helpers.modifiers.modifierTypes.EventType;
import name.modid.helpers.modifiers.modifierTypes.ModifierAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;

public record RubyModifierData() implements GemstonesModifierData {
  private static final Map<GemstoneModifierItemType, GemstoneModifier> MODIFIERS = new HashMap<>();

  static {
    MODIFIERS.put(GemstoneModifierItemType.MELEE,
        new ModifierOnHitEffect(new ArrayList<Double>(Arrays.asList(0.1, 0.2, 0.3, 0.4)), 6, 0,
            GemstoneModifierItemType.MELEE, EffectRegistrationHelper.BLEEDING_EFFECT, true, 5,
            GemstoneType.RUBY));

    MODIFIERS.put(GemstoneModifierItemType.RANGED,
        new ModifierOnHitEffect(new ArrayList<Double>(Arrays.asList(0.2, 0.3, 0.4, 0.5)), 6, 0,
            GemstoneModifierItemType.RANGED, EffectRegistrationHelper.BLEEDING_EFFECT, true, 5,
            GemstoneType.RUBY));

    MODIFIERS.put(GemstoneModifierItemType.TOOLS,
        new ModifierOnBlockBreak(new ArrayList<Double>(Arrays.asList(0.1, 0.2, 0.3, 0.4)),
            new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0, 4.0)),
            GemstoneModifierItemType.TOOLS, EventType.EXTRA_HEALTH, GemstoneType.RUBY));

    MODIFIERS.put(GemstoneModifierItemType.ARMOR, new ModifierAttribute(Operation.ADD_VALUE,
        new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0, 4.0)), GemstoneModifierItemType.ARMOR,
        EntityAttributes.GENERIC_MAX_HEALTH, GemstoneType.RUBY));
  }

  @Override
  public Map<GemstoneModifierItemType, GemstoneModifier> getModifiers() {
    return new HashMap<>(MODIFIERS);
  }
}
