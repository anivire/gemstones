package name.modid.datagen.providers;

import name.modid.Gemstones;
import name.modid.core.content.items.registries.ItemsRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureMap;
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
      String path = Registries.ITEM.getId(item).getPath();
      String baseName = path.replaceAll("_(crude|polished|flawless|radiant|unusual)$", "");

      Models.GENERATED.upload(
          ModelIds.getItemModelId(item),
          TextureMap.layer0(Identifier.of(Gemstones.MOD_ID, "item/" + baseName + ".png")),
          itemModelGenerator.writer);
    }
  }
}
