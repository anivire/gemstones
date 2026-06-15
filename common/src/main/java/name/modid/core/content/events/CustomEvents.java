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
        float damage);
  }

  public static final Event<OnHitMelee> ON_HIT_MELEE = EventFactory.of(
      listeners -> (player, target, damage) -> {
        float result = damage;

        for (OnHitMelee listener : listeners) {
          result = listener.onHitMelee(player, target, result);
        }

        return result;
      });

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
