package name.modid.core.content.registries;

import com.mojang.serialization.Codec;

import name.modid.Gemstones;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public final class AttachmentsRegistry {
  public static final AttachmentType<Boolean> PIERCE_INVULNERABILITY = AttachmentRegistry.create(
      Identifier.of(Gemstones.MOD_ID, "pierce_invulnerability"),
      builder -> builder.initializer(() -> false).persistent(Codec.BOOL));

  public static void initialize() {
    Gemstones.LOGGER.info("Registering attachments for {}", Gemstones.MOD_ID);
  }
}