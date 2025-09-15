package name.modid.core.api.modifiers.categories;

import name.modid.core.api.modifiers.LevelValues;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public sealed interface ModifierConfig
    permits AttributeConfig, HitEffectConfig {
}

final record AttributeConfig(
    LevelValues values,
    Operation operation,
    RegistryEntry<EntityAttribute> attribute) implements ModifierConfig {
}

final record HitEffectConfig(
    LevelValues chance,
    RegistryEntry<StatusEffect> effect,
    int duration,
    int amplifier,
    int maxStacks,
    boolean stacking) implements ModifierConfig {
}