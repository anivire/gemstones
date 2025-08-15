package name.modid.helpers.modifiers.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.ModifierData;
import name.modid.helpers.modifiers.ModifierItemCaregory;
import name.modid.helpers.modifiers.modifierTypes.ModifierAttribute;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;

public record ZirconModifierData() implements ModifierData {
  private static final Map<ModifierItemCaregory, GemstoneModifier> MODIFIERS = new HashMap<>();

  static {
    MODIFIERS.put(ModifierItemCaregory.MELEE, new ModifierAttribute(Operation.ADD_VALUE,
        new ArrayList<Double>(Arrays.asList(0.15, 0.20, 0.25, 0.35)), ModifierItemCaregory.MELEE,
        EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, GemstoneType.ZIRCON));

    MODIFIERS.put(ModifierItemCaregory.RANGED,
        new ModifierAttribute(Operation.ADD_VALUE,
            new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0, 4.0)), ModifierItemCaregory.RANGED,
            EntityAttributes.GENERIC_ATTACK_DAMAGE, GemstoneType.ZIRCON));

    MODIFIERS.put(ModifierItemCaregory.TOOLS,
        new ModifierAttribute(Operation.ADD_VALUE,
            new ArrayList<Double>(Arrays.asList(0.1, 0.15, 0.25, 0.35)), ModifierItemCaregory.TOOLS,
            EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE, GemstoneType.ZIRCON));

    MODIFIERS.put(ModifierItemCaregory.ARMOR, new ModifierAttribute(Operation.ADD_VALUE,
        new ArrayList<Double>(Arrays.asList(0.025, 0.055, 0.075, 0.1)), ModifierItemCaregory.ARMOR,
        EntityAttributes.GENERIC_JUMP_STRENGTH, GemstoneType.ZIRCON));
  }

  @Override
  public Map<ModifierItemCaregory, GemstoneModifier> getModifiers() {
    return new HashMap<>(MODIFIERS);
  }
}
