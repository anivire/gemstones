package name.modid.core.mixins.modifiers;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.OnFirstHitConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;

@Mixin(LivingEntity.class)
public class OnFirstHit {
  @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0)
  private float modifyOnFirstHit(float amount, DamageSource source) {
    LivingEntity target = (LivingEntity) (Object) this;

    if (target.getWorld() instanceof ServerWorld serverWorld
        && source.getAttacker() instanceof LivingEntity owner) {
      List<GemstoneModifier> modifiers = ModifierGatheringHelper.getModifiers(
          source.getWeaponStack(),
          OnFirstHitConfig.class);

      if (modifiers.isEmpty()) {
        return amount;
      }

      ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
          .withOwner(owner)
          .withBaseDamageTaken(amount)
          .withTarget(target);
      ModifierContext ctx = ctxBuilder.build();
      ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);

      return ctx.getDamageResult();
    }

    return amount;
  }
}