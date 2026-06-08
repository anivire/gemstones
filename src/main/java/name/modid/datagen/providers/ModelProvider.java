package name.modid.datagen.providers;

import com.google.gson.JsonObject;

import name.modid.Gemstones;
import name.modid.core.content.blocks.BlocksRegistry;
import name.modid.core.content.items.registries.ItemsRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ModelProvider extends FabricModelProvider {
  public ModelProvider(FabricDataOutput output) {
    super(output);
  }

  @Override
  public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
  }

  @Override
  public void generateItemModels(ItemModelGenerator itemModelGenerator) {
    for (Item item : ItemsRegistry.getAllItems()) {
      if (item instanceof BlockItem) {
        if (item == BlocksRegistry.JEWELRY_TABLE.asItem()) {
          JsonObject model = new JsonObject();
          model.addProperty("parent", "gemstones:block/jewelry_table");
          itemModelGenerator.writer.accept(ModelIds.getItemModelId(item), () -> model);
        }

        continue;
      }

      String path = Registries.ITEM.getId(item).getPath();
      String baseName = path.replaceAll("_(crude|polished|flawless|radiant|unusual)$", "");

      // fix for tools rotation
      var model = isHandheldTool(item) ? Models.HANDHELD : Models.GENERATED;

      model.upload(
          ModelIds.getItemModelId(item),
          TextureMap.layer0(Identifier.of(Gemstones.MOD_ID, "item/" + baseName)),
          itemModelGenerator.writer);
    }
  }

  private boolean isHandheldTool(Item item) {
    return item == ItemsRegistry.DIAMOND_TIPPED_CHISEL
        || item == ItemsRegistry.NETHERITE_TIPPED_CHISEL
        || item == ItemsRegistry.JEWELRY_FILE
        || item == ItemsRegistry.JEWELRY_PLIERS
        || item == ItemsRegistry.JEWELRY_HAMMER;
  }
}
