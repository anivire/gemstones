package name.modid.core.content.loot;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import name.modid.datapack.geodes.GeodesConfig;
import name.modid.datapack.geodes.GeodesRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public record GeodeDropChanceCondition(
    String geodeId,
    float fallbackChance,
    List<TagKey<Block>> fallbackOreTags) implements LootCondition {
  public static final MapCodec<GeodeDropChanceCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      com.mojang.serialization.Codec.STRING.fieldOf("geode_id").forGetter(GeodeDropChanceCondition::geodeId),
      com.mojang.serialization.Codec.FLOAT.optionalFieldOf("base_chance", 0.0f)
          .forGetter(GeodeDropChanceCondition::fallbackChance),
      TagKey.codec(RegistryKeys.BLOCK).listOf().optionalFieldOf("fallback_ore_tags", List.of())
          .forGetter(GeodeDropChanceCondition::fallbackOreTags))
      .apply(instance, GeodeDropChanceCondition::new));

  @Override
  public LootConditionType getType() {
    return LootConditionRegistry.geodeChance();
  }

  @Override
  public boolean test(LootContext context) {
    if (hasSilkTouchTool(context)) {
      return false;
    }

    if (!isConfiguredOre(context)) {
      return false;
    }

    Entity entity = context.get(LootContextParameters.THIS_ENTITY);
    double bonusChance = 0.0f;

    if (entity instanceof PlayerEntity player) {
      double fromArmor = ModifierUtils.collectAttributeValuesFromArmor(
          player, AttributesRegistry.GEODE_DROP_CHANCE_ATTRIBUTE);
      double fromItem = ModifierUtils.collectAttributeValuesFromItem(
          player, AttributesRegistry.GEODE_DROP_CHANCE_ATTRIBUTE);

      bonusChance = fromArmor + fromItem;
    }

    double baseChance = getBaseChance();
    double totalChance = baseChance + bonusChance;

    totalChance = Math.min(totalChance, 1.0);

    Random random = context.getRandom();
    return random.nextDouble() < totalChance;
  }

  private boolean isConfiguredOre(LootContext context) {
    BlockState state = context.get(LootContextParameters.BLOCK_STATE);
    if (state == null) {
      return false;
    }

    GeodesConfig config = GeodesRegistry.getConfig(geodeId);
    if (config != null) {
      if (config.ores.stream().anyMatch(id -> Registries.BLOCK.getId(state.getBlock()).equals(id))) {
        return true;
      }

      if (config.oreTags.stream().map(GeodeDropChanceCondition::blockTag).anyMatch(state::isIn)) {
        return true;
      }

      return config.ores.isEmpty() && config.oreTags.isEmpty()
          && fallbackOreTags.stream().anyMatch(state::isIn);
    }

    return fallbackOreTags.stream().anyMatch(state::isIn);
  }

  private double getBaseChance() {
    GeodesConfig config = GeodesRegistry.getConfig(geodeId);
    if (config != null && config.dropChance != null) {
      return config.dropChance;
    }

    return fallbackChance;
  }

  private boolean hasSilkTouchTool(LootContext context) {
    if (!context.hasParameter(LootContextParameters.TOOL)) {
      return false;
    }

    ItemStack tool = context.get(LootContextParameters.TOOL);
    if (tool.isEmpty()) {
      return false;
    }

    return context.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT)
        .getEntry(Enchantments.SILK_TOUCH)
        .map(silkTouch -> EnchantmentHelper.getLevel(silkTouch, tool) > 0)
        .orElse(false);
  }

  public static LootCondition.Builder builder(String geodeId, float fallbackChance,
      List<TagKey<Block>> fallbackOreTags) {
    return () -> new GeodeDropChanceCondition(geodeId, fallbackChance, fallbackOreTags);
  }

  private static TagKey<Block> blockTag(Identifier id) {
    return TagKey.of(RegistryKeys.BLOCK, id);
  }
}
