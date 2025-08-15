package name.modid.helpers.modifiers.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.ModifierData;
import name.modid.helpers.modifiers.ModifierItemCaregory;
import name.modid.helpers.modifiers.modifierTypes.ConditionType;
import name.modid.helpers.modifiers.modifierTypes.EventType;
import name.modid.helpers.modifiers.modifierTypes.ModifierAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierCustomCondition;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHit;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;

public record AquamarineModifierData() implements ModifierData {
  private static final Map<ModifierItemCaregory, GemstoneModifier> MODIFIERS = new HashMap<>();

  static {
    MODIFIERS.put(ModifierItemCaregory.MELEE,
        new ModifierCustomCondition(new ArrayList<Double>(Arrays.asList(0.1, 0.2, 0.3, 0.4)),
            new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0, 4.0)), ConditionType.PLAYER_IN_WATER,
            ModifierItemCaregory.MELEE, GemstoneType.AQUAMARINE));

    MODIFIERS.put(ModifierItemCaregory.RANGED,
        new ModifierOnHit(new ArrayList<Double>(Arrays.asList(0.15, 0.25, 0.35, 0.45)),
            EventType.TORRENT, ModifierItemCaregory.RANGED, GemstoneType.AQUAMARINE));

    MODIFIERS.put(ModifierItemCaregory.TOOLS, new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
        new ArrayList<Double>(Arrays.asList(0.07, 0.14, 0.21, 0.28)), ModifierItemCaregory.TOOLS,
        EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED, GemstoneType.AQUAMARINE));

    MODIFIERS.put(ModifierItemCaregory.ARMOR, new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
        new ArrayList<Double>(Arrays.asList(0.025, 0.055, 0.075, 0.1)), ModifierItemCaregory.ARMOR,
        EntityAttributes.GENERIC_OXYGEN_BONUS, GemstoneType.AQUAMARINE));
  }

  @Override
  public Map<ModifierItemCaregory, GemstoneModifier> getModifiers() {
    return new HashMap<>(MODIFIERS);
  }
}
