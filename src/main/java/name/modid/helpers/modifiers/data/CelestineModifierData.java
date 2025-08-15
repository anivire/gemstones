package name.modid.helpers.modifiers.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import name.modid.helpers.attributes.AttributeRegistrationHelper;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.ModifierData;
import name.modid.helpers.modifiers.ModifierItemCaregory;
import name.modid.helpers.modifiers.modifierTypes.ModifierAttribute;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;

public class CelestineModifierData implements ModifierData {
  private static final Map<ModifierItemCaregory, GemstoneModifier> MODIFIERS = new HashMap<>();

  static {
    MODIFIERS.put(ModifierItemCaregory.MELEE, new ModifierAttribute(Operation.ADD_MULTIPLIED_BASE,
        new ArrayList<Double>(Arrays.asList(0.05, 0.08, 0.14, 0.20)), ModifierItemCaregory.MELEE,
        EntityAttributes.GENERIC_ATTACK_SPEED, GemstoneType.CELESTINE));

    MODIFIERS.put(ModifierItemCaregory.RANGED,
        new ModifierAttribute(Operation.ADD_MULTIPLIED_BASE,
            new ArrayList<Double>(Arrays.asList(0.1, 0.2, 0.3, 0.45)), ModifierItemCaregory.RANGED,
            AttributeRegistrationHelper.PULL_SPEED_ATTRIBUTE, GemstoneType.CELESTINE));

    MODIFIERS.put(ModifierItemCaregory.TOOLS, new ModifierAttribute(Operation.ADD_MULTIPLIED_BASE,
        new ArrayList<Double>(Arrays.asList(0.08, 0.10, 0.15, 0.25)), ModifierItemCaregory.TOOLS,
        EntityAttributes.PLAYER_BLOCK_BREAK_SPEED, GemstoneType.CELESTINE));

    MODIFIERS.put(ModifierItemCaregory.ARMOR, new ModifierAttribute(Operation.ADD_MULTIPLIED_BASE,
        new ArrayList<Double>(Arrays.asList(0.05, 0.08, 0.12, 0.18)), ModifierItemCaregory.ARMOR,
        EntityAttributes.GENERIC_MOVEMENT_SPEED, GemstoneType.CELESTINE));
  }

  @Override
  public Map<ModifierItemCaregory, GemstoneModifier> getModifiers() {
    return new HashMap<>(MODIFIERS);
  }

}
