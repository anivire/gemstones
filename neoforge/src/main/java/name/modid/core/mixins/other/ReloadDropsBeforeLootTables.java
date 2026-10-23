package name.modid.core.mixins.other;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.datapack.drops.DropsDataLoader;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.ResourceManager;

@Mixin(ReloadableRegistries.class)
public class ReloadDropsBeforeLootTables {
  // Loot tables (and the gemstone pools injected into them) are built before resource reloaders run.
  // Loading the drops configs here makes datapack changes apply on the same reload.
  @Inject(method = "reload", at = @At("HEAD"))
  private static void preloadDropsConfigs(
      CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries,
      ResourceManager resourceManager,
      Executor prepareExecutor,
      CallbackInfoReturnable<CompletableFuture<CombinedDynamicRegistries<ServerDynamicRegistryType>>> cir) {
    DropsDataLoader.preload(resourceManager);
  }
}
