package name.modid.helpers.modifiers.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.GemstoneModifierItemType;
import name.modid.helpers.modifiers.GemstonesModifierData;
import name.modid.helpers.modifiers.modifierTypes.ConditionType;
import name.modid.helpers.modifiers.modifierTypes.EventType;
import name.modid.helpers.modifiers.modifierTypes.ModifierAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierCustomCondition;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHit;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;

public record AquamarineModifierData() implements GemstonesModifierData {
  private static final Map<GemstoneModifierItemType, GemstoneModifier> MODIFIERS = new HashMap<>();

  static {
    MODIFIERS.put(GemstoneModifierItemType.MELEE,
        new ModifierCustomCondition(new ArrayList<Double>(Arrays.asList(0.1, 0.2, 0.3, 0.4)),
            new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0, 4.0)), ConditionType.PLAYER_IN_WATER,
            GemstoneModifierItemType.MELEE, GemstoneType.AQUAMARINE));

    MODIFIERS.put(GemstoneModifierItemType.RANGED,
        new ModifierOnHit(new ArrayList<Double>(Arrays.asList(0.1, 0.2, 0.3, 0.4)),
            EventType.TORRENT, GemstoneModifierItemType.RANGED, GemstoneType.AQUAMARINE));

    MODIFIERS.put(GemstoneModifierItemType.TOOLS,
        new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
            new ArrayList<Double>(Arrays.asList(0.07, 0.14, 0.21, 0.28)),
            GemstoneModifierItemType.TOOLS, EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED,
            GemstoneType.AQUAMARINE));

    MODIFIERS.put(GemstoneModifierItemType.ARMOR,
        new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
            new ArrayList<Double>(Arrays.asList(0.025, 0.055, 0.075, 0.1)),
            GemstoneModifierItemType.ARMOR, EntityAttributes.GENERIC_OXYGEN_BONUS,
            GemstoneType.AQUAMARINE));
  }

  @Override
  public Map<GemstoneModifierItemType, GemstoneModifier> getModifiers() {
    return new HashMap<>(MODIFIERS);
  }
}
