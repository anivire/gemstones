package name.modid;

import java.util.List;
import name.modid.helpers.attributes.AttributeRegistrationHelper;
import name.modid.helpers.particles.BleedParticleFactory;
import name.modid.helpers.particles.ParticlesRegistrationHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class GemstonesClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
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
            if (AttributeRegistrationHelper.PULL_SPEED_ATTRIBUTE == mod.attribute()) {
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
            if (AttributeRegistrationHelper.PULL_SPEED_ATTRIBUTE.equals(mod.attribute())) {
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

    ModelPredicateProviderRegistry.register(Items.BOW, Identifier.of("minecraft", "pulling"),
        (stack, world, entity, seed) -> {
          return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f
              : 0.0f;
        });

    ModelPredicateProviderRegistry.register(Items.CROSSBOW, Identifier.of("minecraft", "pulling"),
        (stack, world, entity, seed) -> entity != null && entity.isUsingItem()
            && entity.getActiveItem() == stack ? 1.0f : 0.0f);

    ParticleFactoryRegistry.getInstance().register(ParticlesRegistrationHelper.BLEED_PARTICLE,
        BleedParticleFactory::new);

    // HudRenderCallback.EVENT.register((context, tickDeltaManager) -> {
    // int color = 0xFFFF0000; // Red
    // int targetColor = 0xFF00FF00; // Green

    // // Total tick delta is stored in a field, so we can use it later.
    // float totalTickDelta = tickDeltaManager.getTickDelta(true);

    // // "lerp" simply means "linear interpolation", which is a fancy way of saying "blend".
    // float lerpedAmount = MathHelper.abs(MathHelper.sin(totalTickDelta / 50F));
    // int lerpedColor = ColorHelper.Argb.lerp(lerpedAmount, color, targetColor);

    // // Draw a square with the lerped color.
    // // x1, x2, y1, y2, z, color
    // context.fill(0, 0, 100, 100, 0, lerpedColor);
    // });
  }
}
