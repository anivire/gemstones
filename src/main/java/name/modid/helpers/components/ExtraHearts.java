package name.modid.helpers.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import name.modid.Gemstones;
import name.modid.helpers.attributes.AttributeRegistrationHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public record ExtraHearts(int value) {
  public static final Codec<ExtraHearts> CODEC = RecordCodecBuilder
      .create(instance -> instance.group(Codec.INT.fieldOf("value").forGetter(ExtraHearts::value))
          .apply(instance, ExtraHearts::new));

  public void apply(LivingEntity entity) {
    EntityAttributeInstance healthAttribute =
        entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
    if (healthAttribute == null)
      return;

    healthAttribute.removeModifier(Identifier.of(Gemstones.MOD_ID, "extra_hearts"));

    if (value > 0) {
      EntityAttributeModifier modifier = new EntityAttributeModifier(
          Identifier.of(Gemstones.MOD_ID, "extra_hearts"), value * 1.0, Operation.ADD_VALUE);
      healthAttribute.addPersistentModifier(modifier);
      entity.setHealth(Math.min(entity.getHealth(), entity.getMaxHealth()));
    }
  }


  public void reduceHearts(LivingEntity entity, int amount) {
    int newValue = Math.max(0, value - amount);
    ExtraHearts newHearts = new ExtraHearts(newValue);
    entity.setAttached(ComponentsHelper.EXTRA_HEARTS, newHearts);
    newHearts.apply(entity);
  }

  public static float getTotalMaxHealth(PlayerEntity player) {
    EntityAttributeInstance healthAttribute =
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
    EntityAttributeInstance customHealthAttribute =
        player.getAttributeInstance(AttributeRegistrationHelper.EXTRA_HEARTS_ATTRIBUTE);
    float totalHealth = 0.0f;
    if (healthAttribute != null) {
      totalHealth += healthAttribute.getValue();
    }
    if (customHealthAttribute != null) {
      totalHealth += customHealthAttribute.getValue();
    }
    return totalHealth;
  }

  public static ExtraHearts get(LivingEntity entity) {
    return entity.getAttached(ComponentsHelper.EXTRA_HEARTS);
  }

  public static void set(LivingEntity entity, int value) {
    entity.setAttached(ComponentsHelper.EXTRA_HEARTS, new ExtraHearts(value));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ExtraHearts that = (ExtraHearts) o;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(value);
  }
}
