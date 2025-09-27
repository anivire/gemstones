package name.modid.core.api.modifiers.config;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class ModifierContext {
  private final ServerWorld world;
  private final @Nullable Entity owner;
  private final @Nullable Entity target;
  private final @Nullable BlockPos blockPos;
  private final @Nullable PersistentProjectileEntity projectile;
  private final @Nullable BlockState blockState;
  private final @Nullable float baseDamageTaken;
  private final @Nullable DefaultedList<ItemStack> inventory;

  private boolean cancelledResult = false;
  private float damageResult = 0.0F;
  private ActionResult actionResult = ActionResult.PASS;

  private ModifierContext(ContextBuilder builder) {
    this.world = builder.world;
    this.owner = builder.owner;
    this.target = builder.target;
    this.blockPos = builder.blockPos;
    this.projectile = builder.projectile;
    this.blockState = builder.blockState;
    this.baseDamageTaken = builder.baseDamageTaken;
    this.inventory = builder.inventory;
  }

  public boolean isCancelled() {
    return this.cancelledResult;
  }

  public void cancel() {
    this.cancelledResult = true;
  }

  public float getDamageResult() {
    return this.damageResult;
  }

  public void setDamageResult(float newDamage) {
    this.damageResult = newDamage;
  }

  public ActionResult getActionResult() {
    return this.actionResult;
  }

  public void setActionResult(ActionResult result) {
    if (result.ordinal() > this.actionResult.ordinal()) {
      this.actionResult = result;
    }
  }

  public ServerWorld getWorld() {
    return this.world;
  }

  @Nullable
  public Entity getOwner() {
    return this.owner;
  }

  @Nullable
  public Entity getTarget() {
    return this.target;
  }

  @Nullable
  public BlockPos getBlockPos() {
    return this.blockPos;
  }

  @Nullable
  public BlockState getBlockState() {
    return this.blockState;
  }

  @Nullable
  public PersistentProjectileEntity getProjectile() {
    return this.projectile;
  }

  @Nullable
  public float getBaseDamageTaken() {
    return this.baseDamageTaken;
  }

  @Nullable
  public DefaultedList<ItemStack> getInventory() {
    return this.inventory;
  }

  public static class ContextBuilder {
    private final ServerWorld world;
    private @Nullable Entity owner;
    private @Nullable Entity target;
    private @Nullable BlockPos blockPos;
    private @Nullable PersistentProjectileEntity projectile;
    private @Nullable BlockState blockState;
    private @Nullable float baseDamageTaken;
    private @Nullable DefaultedList<ItemStack> inventory;

    public ContextBuilder(ServerWorld world) {
      this.world = world;
    }

    public ContextBuilder withOwner(Entity owner) {
      this.owner = owner;
      return this;
    }

    public ContextBuilder withTarget(Entity target) {
      this.target = target;
      return this;
    }

    public ContextBuilder withBlockPos(BlockPos pos) {
      this.blockPos = pos;
      return this;
    }

    public ContextBuilder withProjectile(PersistentProjectileEntity projectile) {
      this.projectile = projectile;
      return this;
    }

    public ContextBuilder withBlockState(BlockState blockState) {
      this.blockState = blockState;
      return this;
    }

    public ContextBuilder withBaseDamageTaken(float baseDamageTaken) {
      this.baseDamageTaken = baseDamageTaken;
      return this;
    }

    public ContextBuilder withInventory(DefaultedList<ItemStack> inventory) {
      this.inventory = inventory;
      return this;
    }

    public ModifierContext build() {
      return new ModifierContext(this);
    }
  }
}