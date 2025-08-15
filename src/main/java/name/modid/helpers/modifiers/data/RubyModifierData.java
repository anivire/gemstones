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
import name.modid.helpers.modifiers.modifierTypes.ModifierMultiplyAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffectProjectile;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;

public record RubyModifierData() implements ModifierData {
  private static final Map<ModifierItemCaregory, GemstoneModifier> MODIFIERS = new HashMap<>();

  static {
    MODIFIERS.put(ModifierItemCaregory.MELEE,
        new ModifierOnHitEffect(new ArrayList<Double>(Arrays.asList(0.08, 0.15, 0.25, 0.3)), 6, 0,
            ModifierItemCaregory.MELEE, EffectRegistrationHelper.BLEEDING_EFFECT, true, 5,
            GemstoneType.RUBY));

    MODIFIERS.put(ModifierItemCaregory.RANGED,
        new ModifierOnHitEffectProjectile(
            new ArrayList<Double>(Arrays.asList(0.16, 0.25, 0.35, 0.45)), 6, 0,
            ModifierItemCaregory.RANGED, EffectRegistrationHelper.BLEEDING_EFFECT, true, 5,
            GemstoneType.RUBY));

    MODIFIERS.put(ModifierItemCaregory.TOOLS,
        new ModifierOnBlockBreak(new ArrayList<Double>(Arrays.asList(0.1, 0.2, 0.3, 0.4)),
            new ArrayList<Double>(Arrays.asList(1.0, 1.0, 2.0, 2.0)), ModifierItemCaregory.TOOLS,
            EventType.EXTRA_HEALTH, GemstoneType.RUBY));

    MODIFIERS.put(ModifierItemCaregory.ARMOR, new ModifierMultiplyAttribute(
        new ArrayList<ModifierAttribute>(Arrays.asList(new ModifierAttribute(Operation.ADD_VALUE,
            new ArrayList<Double>(Arrays.asList(0.5, 0.5, 1.0, 1.0)), ModifierItemCaregory.ARMOR,
            EntityAttributes.GENERIC_ATTACK_DAMAGE, GemstoneType.RUBY),
            new ModifierAttribute(Operation.ADD_VALUE,
                new ArrayList<Double>(Arrays.asList(-2.0, -1.0, -2.0, -2.0)),
                ModifierItemCaregory.ARMOR, EntityAttributes.GENERIC_MAX_HEALTH,
                GemstoneType.RUBY)))));
  }

  @Override
  public Map<ModifierItemCaregory, GemstoneModifier> getModifiers() {
    return new HashMap<>(MODIFIERS);
  }
}
