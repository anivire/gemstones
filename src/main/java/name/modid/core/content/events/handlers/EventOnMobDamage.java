package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.OnMobDamageConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class EventOnMobDamage {
  public static void setup(
      LivingEntity targetEntity,
      DamageSource source,
      float baseDamageTaken,
      float damageTaken,
      boolean blocked) {

    // Проверяем, что атакующий — игрок
    if (!(source.getAttacker() instanceof ServerPlayerEntity serverPlayer)
        || !(targetEntity.getWorld() instanceof ServerWorld serverWorld)) {
      return;
    }

    // Берем модификаторы с экипировки атакующего игрока
    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer, OnMobDamageConfig.class);

    if (modifiers.isEmpty()) {
      return;
    }

    // Формируем контекст: игрок — владелец эффекта, цель — моб, получающий урон
    ModifierContext.ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(serverPlayer)
        .withTarget(targetEntity)
        .withBaseDamageTaken(baseDamageTaken);

    if (source.getSource() instanceof PersistentProjectileEntity proj) {
      ctxBuilder.withProjectile(proj);
    }

    ModifierContext ctx = ctxBuilder.build();
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);
  }
}