package name.modid.core.api.modifiers.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.config.handlers.AttributeModifierHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class ModifierManager {
  public static void applyAttributeModifiers(ArrayList<GemstoneModifier> modifiers, ItemStack stack) {
    ArrayList<GemstoneModifier> attributeModifiers = new ArrayList<>(modifiers.stream()
        .filter(x -> x.getConfig() instanceof ModifierConfig.AttributeConfig)
        .toList());

    List<GemstoneModifier> attributeMultModifiers = modifiers.stream()
        .filter(m -> m.getConfig() instanceof ModifierConfig.MultiplyAttributeConfig)
        .flatMap(m -> {
          ModifierConfig.MultiplyAttributeConfig mac = (ModifierConfig.MultiplyAttributeConfig) m.getConfig();
          return mac.instances().stream().map(attrCfg -> new GemstoneModifier(
              m.getGemstoneType(),
              m.getRarityType(),
              m.getItemCategory(),
              attrCfg));
        })
        .toList();

    List<GemstoneModifier> merged = Stream.concat(attributeModifiers.stream(), attributeMultModifiers.stream())
        .toList();

    AttributeModifierHandler.apply(new ArrayList<>(merged), stack);
  }

  public static void applyModifiers(ArrayList<GemstoneModifier> allModifiers, ModifierContext ctx) {
    Map<Class<? extends ModifierConfig>, List<GemstoneModifier>> groupedModifiers = allModifiers.stream()
        .collect(Collectors.groupingBy(modifier -> modifier.getConfig().getClass()));

    for (List<GemstoneModifier> modifierGroup : groupedModifiers.values()) {
      if (modifierGroup.isEmpty()) {
        continue;
      }

      ModifierConfig config = modifierGroup.get(0).getConfig();
      ModifierHandler<ModifierConfig> handler = ModifierHandlerRegistry.getHandler(config);

      if (handler != null) {
        handler.apply(new ArrayList<>(modifierGroup), ctx);

        // Check if a handler has cancelled the event or set a failing result.
        // If so, we stop processing any further modifier groups.
        if (ctx.isCancelled() || ctx.getActionResult() == ActionResult.FAIL) {
          return;
        }
      } else {
        Gemstones.LOGGER.warn("No handler found for modifier config: {}",
            config.getClass().getSimpleName());
      }
    }
  }
}