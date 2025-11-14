package name.modid.core.content.registries;

import name.modid.Gemstones;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class TagsRegistry {
  public static final TagKey<Block> ALL_ORES = TagKey.of(
      RegistryKeys.BLOCK,
      Identifier.of(Gemstones.MOD_ID, "all_ores"));

  public static final TagKey<Block> DEEPSLATE_ORES = TagKey.of(
      RegistryKeys.BLOCK,
      Identifier.of(Gemstones.MOD_ID, "deepslate_ores"));

  public static final TagKey<StatusEffect> STUNNED_EFFECT_TAG = TagKey.of(
      RegistryKeys.STATUS_EFFECT,
      Identifier.of(Gemstones.MOD_ID, "stunned_effect_tag"));

  public static void initialize() {
    Gemstones.LOGGER.info("Registering tags for {}", Gemstones.MOD_ID);
  }
}