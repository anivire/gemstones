package name.modid.core.api.models;

import java.util.List;

import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class CrossbowModel {
  public static void register() {
    ModelPredicateProviderRegistry.register(Items.CROSSBOW, Identifier.of("minecraft", "pull"),
        (itemStack, world, entity, seed) -> {
          if (entity == null) {
            return 0.0f;
          }

          float drawSpeedPercent = 0.0f;
          AttributeModifiersComponent itemAttributeModifiers = itemStack.getOrDefault(
              DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
          List<Entry> modifiers = itemAttributeModifiers.modifiers();

          for (Entry mod : modifiers) {
            if (AttributesRegistry.PULL_SPEED_ATTRIBUTE.equals(mod.attribute())) {
              drawSpeedPercent += (float) mod.modifier().value();
            }
          }

          float baseChargeTime = EnchantmentHelper.getCrossbowChargeTime(itemStack, entity, 1.25f);
          float modifiedChargeTime = baseChargeTime / (1.0f + drawSpeedPercent);
          float modifiedChargeTicks = modifiedChargeTime * 20.0f;
          float useTicks = itemStack.getMaxUseTime(entity) - entity.getItemUseTimeLeft();
          float progress = useTicks / modifiedChargeTicks;
          float finalProgress = MathHelper.clamp(progress, 0.0f, 1.0f);

          return finalProgress;
        });

    ModelPredicateProviderRegistry.register(Items.CROSSBOW, Identifier.of("minecraft", "pulling"),
        (stack, world, entity, seed) -> entity != null && entity.isUsingItem()
            && entity.getActiveItem() == stack ? 1.0f : 0.0f);
  }
}
