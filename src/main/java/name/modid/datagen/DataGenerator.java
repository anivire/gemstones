package name.modid.datagen;

import name.modid.datagen.providers.BlockTagProvider;
import name.modid.datagen.providers.ModelProvider;
import name.modid.datagen.providers.MossyBoxLootTableProvider;
import name.modid.datagen.providers.RecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGenerator implements DataGeneratorEntrypoint {
  @Override
  public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
    FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

    pack.addProvider(ModelProvider::new);
    pack.addProvider(BlockTagProvider::new);
    pack.addProvider(MossyBoxLootTableProvider::new);
    pack.addProvider(RecipeProvider::new);
  }
}
