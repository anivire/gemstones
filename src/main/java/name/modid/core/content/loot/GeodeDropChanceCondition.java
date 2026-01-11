package name.modid.core.content.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.random.Random;

public record GeodeDropChanceCondition(float baseChance) implements LootCondition {
  public static final MapCodec<GeodeDropChanceCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      com.mojang.serialization.Codec.FLOAT.fieldOf("base_chance").forGetter(GeodeDropChanceCondition::baseChance))
      .apply(instance, GeodeDropChanceCondition::new));

  @Override
  public LootConditionType getType() {
    return LootConditionRegistry.GEODE_CHANCE;
  }

  @Override
  public boolean test(LootContext context) {
    Entity entity = context.get(LootContextParameters.THIS_ENTITY);
    double bonusChance = 0.0f;

    if (entity instanceof PlayerEntity player) {
      double fromArmor = ModifierUtils.collectAttributeValuesFromArmor(
          player, AttributesRegistry.GEODE_DROP_CHANCE_ATTRIBUTE);
      double fromItem = ModifierUtils.collectAttributeValuesFromItem(
          player, AttributesRegistry.GEODE_DROP_CHANCE_ATTRIBUTE);

      bonusChance = fromArmor + fromItem;
    }

    double totalChance = this.baseChance + bonusChance;
    Random random = context.getRandom();

    return random.nextDouble() < totalChance;
  }

  public static LootCondition.Builder builder(float baseChance) {
    return () -> new GeodeDropChanceCondition(baseChance);
  }
}