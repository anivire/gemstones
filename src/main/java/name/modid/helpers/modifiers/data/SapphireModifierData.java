package name.modid.helpers.modifiers.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import name.modid.entities.EffectRegistrationHelper;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.GemstoneModifierItemType;
import name.modid.helpers.modifiers.GemstonesModifierData;
import name.modid.helpers.modifiers.modifierTypes.ModifierAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHit;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHit.EventType;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;

public record SapphireModifierData() implements GemstonesModifierData {
  private static final Map<GemstoneModifierItemType, GemstoneModifier> MODIFIERS = new HashMap<>();

  static {
    MODIFIERS.put(GemstoneModifierItemType.MELEE,
        new ModifierOnHitEffect(new ArrayList<Double>(Arrays.asList(0.1, 0.2, 0.3, 0.4)), 6, 0,
            GemstoneModifierItemType.MELEE, EffectRegistrationHelper.GUARDIAN_SMITE_EFFECT, false,
            1, GemstoneType.RUBY));

    MODIFIERS.put(GemstoneModifierItemType.RANGED,
        new ModifierOnHit(new ArrayList<Double>(Arrays.asList(0.05, 0.10, 0.15, 0.25)),
            EventType.LIGHTNING_BOLT, GemstoneModifierItemType.RANGED, GemstoneType.SAPPHIRE));

    MODIFIERS.put(GemstoneModifierItemType.TOOLS,
        new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
            new ArrayList<Double>(Arrays.asList(0.15, 0.20, 0.25, 0.30)),
            GemstoneModifierItemType.TOOLS, EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED,
            GemstoneType.SAPPHIRE));

    MODIFIERS.put(GemstoneModifierItemType.ARMOR,
        new ModifierAttribute(Operation.ADD_MULTIPLIED_TOTAL,
            new ArrayList<Double>(Arrays.asList(0.10, 0.15, 0.20, 0.30)),
            GemstoneModifierItemType.ARMOR, EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY,
            GemstoneType.SAPPHIRE));
  }

  @Override
  public Map<GemstoneModifierItemType, GemstoneModifier> getModifiers() {
    return new HashMap<>(MODIFIERS);
  }
}
