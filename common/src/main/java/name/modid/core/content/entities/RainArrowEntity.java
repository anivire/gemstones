package name.modid.core.content.entities;

import name.modid.core.content.registries.EntitiesRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RainArrowEntity extends ArrowEntity {
  private static final int MAX_TICKS_ALIVE = 200;

  public RainArrowEntity(EntityType<? extends RainArrowEntity> entityType, World world) {
    super(entityType, world);
  }

  public RainArrowEntity(World world, double x, double y, double z, ItemStack stack) {
    super(world, x, y, z, stack.copy(), null);
  }

  public RainArrowEntity(World world, LivingEntity owner, ItemStack stack) {
    super(world, owner, stack.copy(), null);
  }

  @Override
  public EntityType<?> getType() {
    return EntitiesRegistry.RAIN_ARROW.get();
  }

  @Override
  public void tick() {
    super.tick();

    if (!this.getWorld().isClient() && this.age > MAX_TICKS_ALIVE) {
      this.discard();
    }
  }
}