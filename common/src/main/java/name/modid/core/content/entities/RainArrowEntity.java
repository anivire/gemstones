package name.modid.core.content.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class RainArrowEntity extends ArrowEntity {
  private boolean gemstonesApplied = false;
  private int ttl = 100;
  private int slowDuration = 0;
  private int slowAmplifier = 0;

  public RainArrowEntity(EntityType<? extends ArrowEntity> type, World world) {
    super(type, world);
    this.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
  }

  public RainArrowEntity(World world, double x, double y, double z, ItemStack stack) {
    super(world, x, y, z, stack, null);
    this.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
  }

  public void setRainSlowness(int durationTicks, int amplifier) {
    this.slowDuration = Math.max(1, durationTicks);
    this.slowAmplifier = Math.max(0, amplifier);
  }

  @Override
  protected void onEntityHit(EntityHitResult hit) {
    super.onEntityHit(hit);
    if (this.getWorld().isClient())
      return;

    if (!gemstonesApplied && hit.getEntity() instanceof LivingEntity living) {
      living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, slowDuration, slowAmplifier));
      gemstonesApplied = true;
      this.discard();
    }
  }

  @Override
  public void tick() {
    super.tick();
    if (this.getWorld().isClient())
      return;

    if (ttl-- <= 0) {
      this.discard();
    }
  }
}