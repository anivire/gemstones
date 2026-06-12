package name.modid.core.content.registries;

import name.modid.Gemstones;

public final class AttachmentsRegistry {
  private AttachmentsRegistry() {
  }

  public static void initialize() {
    Gemstones.LOGGER.info("Registering attachments for {}", Gemstones.MOD_ID);
  }
}
