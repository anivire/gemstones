package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.OnMobDamageConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.utils.damage.DamageSourceHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class EventOnMobDamage {
  // Called before the damage is applied, returns the damage with the bonus added into the hit
  public static float setup(
      LivingEntity targetEntity,
      DamageSource source,
      float baseDamageTaken) {

    // Проверяем, что атакующий — игрок, и это прямой удар или стрела, а не DoT или бонусный урон
    if (!(source.getAttacker() instanceof ServerPlayerEntity serverPlayer)
        || !DamageSourceHelper.isDirectPlayerHit(source)
        || !(targetEntity.getWorld() instanceof ServerWorld serverWorld)) {
      return baseDamageTaken;
    }

    // Берем модификаторы с экипировки атакующего игрока
    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer, OnMobDamageConfig.class);

    if (modifiers.isEmpty()) {
      return baseDamageTaken;
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
    ctx.setDamageResult(baseDamageTaken);
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);

    return ctx.getDamageResult();
  }
}