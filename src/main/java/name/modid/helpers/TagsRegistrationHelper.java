package name.modid.helpers;

import name.modid.Gemstones;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

// TODO: add tags translation
public class TagsRegistrationHelper {
  // TODO: add stunned tag
  public static final TagKey<Block> ALL_ORES = TagKey.of(
      RegistryKeys.BLOCK,
      Identifier.of(Gemstones.MOD_ID, "all_ores"));

  public static final TagKey<Block> DEEPSLATE_ORES = TagKey.of(
      RegistryKeys.BLOCK,
      Identifier.of(Gemstones.MOD_ID, "deepslate_ores"));

  public static void initialize() {
    Gemstones.LOGGER.info("Registering tags for {}", Gemstones.MOD_ID);
  }
}