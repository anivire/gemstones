package name.modid.helpers.modifiers.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import name.modid.effects.EffectRegistrationHelper;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.ModifierData;
import name.modid.helpers.modifiers.ModifierItemCaregory;
import name.modid.helpers.modifiers.modifierTypes.EventType;
import name.modid.helpers.modifiers.modifierTypes.ModifierAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHit;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;

public record SapphireModifierData() implements ModifierData {
  private static final Map<ModifierItemCaregory, GemstoneModifier> MODIFIERS = new HashMap<>();

  static {
    MODIFIERS.put(ModifierItemCaregory.MELEE,
        new ModifierOnHitEffect(new ArrayList<Double>(Arrays.asList(0.08, 0.16, 0.24, 0.36)), 6, 0,
            ModifierItemCaregory.MELEE, EffectRegistrationHelper.GUARDIAN_SMITE_EFFECT, false, 1,
            GemstoneType.RUBY));

    MODIFIERS.put(ModifierItemCaregory.RANGED,
        new ModifierOnHit(new ArrayList<Double>(Arrays.asList(0.05, 0.10, 0.15, 0.25)),
            EventType.LIGHTNING_BOLT, ModifierItemCaregory.RANGED, GemstoneType.SAPPHIRE));

    MODIFIERS.put(ModifierItemCaregory.TOOLS, new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
        new ArrayList<Double>(Arrays.asList(0.15, 0.20, 0.25, 0.30)), ModifierItemCaregory.TOOLS,
        EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED, GemstoneType.SAPPHIRE));

    MODIFIERS.put(ModifierItemCaregory.ARMOR, new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
        new ArrayList<Double>(Arrays.asList(0.10, 0.15, 0.20, 0.30)), ModifierItemCaregory.ARMOR,
        EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY, GemstoneType.SAPPHIRE));
  }

  @Override
  public Map<ModifierItemCaregory, GemstoneModifier> getModifiers() {
    return new HashMap<>(MODIFIERS);
  }
}
