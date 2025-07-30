package name.modid.helpers.modifiers.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.GemstonesModifierData;
import name.modid.helpers.modifiers.GemstoneModifierItemType;
import name.modid.helpers.modifiers.types.ModifierAttribute;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;

public record AquamarinModifierData() implements GemstonesModifierData {
  private static final Map<GemstoneModifierItemType, GemstoneModifier> MODIFIERS = new HashMap<>();

  static {
    MODIFIERS.put(GemstoneModifierItemType.MELEE,
        new ModifierAttribute(Operation.ADD_VALUE, new ArrayList<Double>(Arrays.asList(0.5, 1.0, 1.55, 1.9)),
            GemstoneModifierItemType.MELEE, EntityAttributes.GENERIC_ATTACK_DAMAGE, GemstoneType.AQUAMARIN));

    MODIFIERS.put(GemstoneModifierItemType.RANGED,
        new ModifierAttribute(Operation.ADD_VALUE, new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0, 4.0)),
            GemstoneModifierItemType.RANGED, EntityAttributes.GENERIC_ATTACK_DAMAGE, GemstoneType.AQUAMARIN));

    MODIFIERS.put(GemstoneModifierItemType.TOOLS,
        new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
            new ArrayList<Double>(Arrays.asList(0.07, 0.14, 0.21, 0.28)), GemstoneModifierItemType.TOOLS,
            EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED, GemstoneType.AQUAMARIN));

    MODIFIERS.put(GemstoneModifierItemType.ARMOR,
        new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
            new ArrayList<Double>(Arrays.asList(0.025, 0.055, 0.075, 0.1)), GemstoneModifierItemType.ARMOR,
            EntityAttributes.GENERIC_OXYGEN_BONUS, GemstoneType.AQUAMARIN));
  }

  @Override
  public Map<GemstoneModifierItemType, GemstoneModifier> getModifiers() {
    return new HashMap<>(MODIFIERS);
  }
}
