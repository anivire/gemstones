package name.modid.core.mixins.effects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public class GuardianSmiteEffect {
  private static final ThreadLocal<Boolean> IS_APPLYING_GUARDIAN_SMITE = ThreadLocal.withInitial(() -> false);

  // While affected by Guardian Smite debuff, receiving bonus magic damage on hit.
  @Inject(method = "damage", at = @At("RETURN"))
  private void bonusMagic(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    if (IS_APPLYING_GUARDIAN_SMITE.get()) {
      return;
    }

    if (!cir.getReturnValue()) {
      return;
    }

    LivingEntity target = (LivingEntity) (Object) this;
    StatusEffectInstance effect = target.getStatusEffect(EffectsRegistry.guardianSmiteEntry());

    if (effect == null) {
      return;
    }

    World world = target.getWorld();

    if (world.isClient()) {
      return;
    }

    MinecraftServer server = world.getServer();

    if (server == null) {
      return;
    }

    float bonusDamage = 3.0F * (effect.getAmplifier() + 1);

    server.execute(() -> {
      if (!target.isAlive()) {
        return;
      }

      IS_APPLYING_GUARDIAN_SMITE.set(true);
      try {
        target.hurtTime = 0;
        target.timeUntilRegen = 0;
        target.damage(target.getDamageSources().magic(), bonusDamage);
      } finally {
        IS_APPLYING_GUARDIAN_SMITE.set(false);
      }
    });
  }
}