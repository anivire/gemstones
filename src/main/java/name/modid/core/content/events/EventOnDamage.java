package name.modid.core.content.events;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.OnDamageConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.ModifierUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EventOnDamage {
  public static void setup(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken,
      boolean blocked) {
    World world = entity.getWorld();

    if (world instanceof ServerWorld serverWorld) {
      LivingEntity attacker = (LivingEntity) source.getAttacker();
      if (!(attacker instanceof ServerPlayerEntity player)) {
        return;
      }

      List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
          player, OnDamageConfig.class);

      // Fallback: Если метод не работает, используйте generic
      // List<GemstoneModifier> modifiers =
      // ModifierUtils.collectValuesFromAllEquipment(
      // player,
      // itemStack -> ModifierGatheringHelper.getModifiers(itemStack,
      // OnDamageConfig.class)
      // );

      if (modifiers.isEmpty()) {
        return;
      }

      PersistentProjectileEntity proj = null;
      if (source.getSource() instanceof PersistentProjectileEntity p) {
        proj = p;
      }

      ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
          .withOwner(player)
          .withProjectile(proj)
          .withBaseDamageTaken(baseDamageTaken)
          .withTarget(entity);

      ModifierContext ctx = ctxBuilder.build();
      ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);
    }
  }
}
