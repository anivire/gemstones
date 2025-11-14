package name.modid.core.api.models;

import java.util.List;

import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class BowModel {
  public static void register() {
    ModelPredicateProviderRegistry.register(Items.BOW, Identifier.of("minecraft", "pull"),
        (itemStack, world, entity, seed) -> {
          if (entity == null) {
            return 0.0f;
          }

          float drawSpeedPercent = 0.00f;

          AttributeModifiersComponent itemAttributeModifiers = itemStack.getOrDefault(
              DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
          List<Entry> modifiers = itemAttributeModifiers.modifiers();

          for (Entry mod : modifiers) {
            if (AttributesRegistry.PULL_SPEED_ATTRIBUTE == mod.attribute()) {
              drawSpeedPercent += (float) mod.modifier().value();
            }
          }

          float useTicks = itemStack.getMaxUseTime(entity) - entity.getItemUseTimeLeft();
          float adjustedTicks = useTicks * (1.0f + drawSpeedPercent);
          float progress = adjustedTicks / 20.0f;
          progress = (progress * progress + progress * 2.0f) / 3.0f;
          if (progress > 1.0f) {
            progress = 1.0f;
          }
          return progress;
        });

    ModelPredicateProviderRegistry.register(Items.BOW, Identifier.of("minecraft", "pulling"),
        (stack, world, entity, seed) -> {
          return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f
              : 0.0f;
        });
  }
}
