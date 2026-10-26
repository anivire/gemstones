package name.modid.compat.cloth;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import name.modid.core.config.GemstonesClientConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class GemstonesConfigScreen {
  private GemstonesConfigScreen() {
  }

  public static Screen create(Screen parent) {
    GemstonesClientConfig.Values values = GemstonesClientConfig.get();
    GemstonesClientConfig.Values defaults = new GemstonesClientConfig.Values();

    ConfigBuilder builder = ConfigBuilder.create()
        .setParentScreen(parent)
        .setTitle(Text.translatable("config.gemstones.title"))
        .setSavingRunnable(GemstonesClientConfig::save);
    ConfigEntryBuilder entries = builder.entryBuilder();

    ConfigCategory tooltips = builder.getOrCreateCategory(Text.translatable("config.gemstones.category.tooltips"));
    tooltips.addEntry(entries
        .startBooleanToggle(Text.translatable("config.gemstones.option.show_empty_sockets"), values.showEmptySockets)
        .setDefaultValue(defaults.showEmptySockets)
        .setTooltip(Text.translatable("config.gemstones.option.show_empty_sockets.tooltip"))
        .setSaveConsumer(value -> values.showEmptySockets = value)
        .build());

    return builder.build();
  }
}
