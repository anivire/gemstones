package name.modid.core.content.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.HitResult;

public final class CustomEvents {
  private CustomEvents() {
  }

  @FunctionalInterface
  public interface OnFirstHit {
    float onFirstHit(LivingEntity target, DamageSource source, float amount);
  }

  public static final Event<OnFirstHit> ON_FIRST_HIT = EventFactory.of(
      listeners -> (target, source, amount) -> {
        float result = amount;

        for (OnFirstHit listener : listeners) {
          result = listener.onFirstHit(target, source, result);
        }

        return result;
      });

  @FunctionalInterface
  public interface OnFishing {
    void onFishing(ServerPlayerEntity player, FishingBobberEntity bobber);
  }

  public static final Event<OnFishing> ON_FISHING = EventFactory.of(
      listeners -> (player, bobber) -> {
        for (OnFishing listener : listeners) {
          listener.onFishing(player, bobber);
        }
      });

  @FunctionalInterface
  public interface OnHitMelee {
    float onHitMelee(
        ServerPlayerEntity player,
        LivingEntity target,
        float damage,
        float attackStrength);
  }

  public static final Event<OnHitMelee> ON_HIT_MELEE = EventFactory.of(
      listeners -> (player, target, damage, attackStrength) -> {
        float result = damage;

        for (OnHitMelee listener : listeners) {
          result = listener.onHitMelee(player, target, result, attackStrength);
        }

        return result;
      });

  // Fired before the projectile damage is calculated, used only for damage modifiers
  @FunctionalInterface
  public interface OnProjectileDamage {
    void onProjectileDamage(
        PersistentProjectileEntity projectile,
        ServerPlayerEntity player,
        HitResult hitResult);
  }

  public static final Event<OnProjectileDamage> ON_PROJECTILE_DAMAGE = EventFactory.of(
      listeners -> (projectile, player, hitResult) -> {
        for (OnProjectileDamage listener : listeners) {
          listener.onProjectileDamage(projectile, player, hitResult);
        }
      });

  // Fired only after the projectile has actually damaged a living target
  @FunctionalInterface
  public interface OnHitProjectile {
    void onHitProjectile(
        PersistentProjectileEntity projectile,
        ServerPlayerEntity player,
        HitResult hitResult);
  }

  public static final Event<OnHitProjectile> ON_HIT_PROJECTILE = EventFactory.of(
      listeners -> (projectile, player, hitResult) -> {
        for (OnHitProjectile listener : listeners) {
          listener.onHitProjectile(projectile, player, hitResult);
        }
      });

  // Fired before the damage is applied, used for bonus damage that is added into the hit itself
  @FunctionalInterface
  public interface OnOutgoingDamage {
    float onOutgoingDamage(
        LivingEntity target,
        DamageSource source,
        float amount);
  }

  public static final Event<OnOutgoingDamage> ON_OUTGOING_DAMAGE = EventFactory.of(
      listeners -> (target, source, amount) -> {
        float result = amount;

        for (OnOutgoingDamage listener : listeners) {
          result = listener.onOutgoingDamage(target, source, result);
        }

        return result;
      });

  // Fired after a living entity has actually taken damage (not blocked by i-frames or a shield)
  @FunctionalInterface
  public interface OnLivingDamaged {
    void onLivingDamaged(
        LivingEntity target,
        DamageSource source,
        float amount);
  }

  public static final Event<OnLivingDamaged> ON_LIVING_DAMAGED = EventFactory.of(
      listeners -> (target, source, amount) -> {
        for (OnLivingDamaged listener : listeners) {
          listener.onLivingDamaged(target, source, amount);
        }
      });

  @FunctionalInterface
  public interface OnPotionBrew {
    void onPotionBrew(
        ServerPlayerEntity player,
        DefaultedList<ItemStack> inventory);
  }

  public static final Event<OnPotionBrew> ON_POTION_BREW = EventFactory.of(
      listeners -> (player, inventory) -> {
        for (OnPotionBrew listener : listeners) {
          listener.onPotionBrew(player, inventory);
        }
      });
}
