package name.modid.core.api.modifiers.context;

import org.jetbrains.annotations.Nullable;

import name.modid.core.api.modifiers.GemstoneQuality;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record ModifierContext(
    ServerWorld world,
    @Nullable LivingEntity attacker,
    @Nullable LivingEntity target,
    @Nullable Entity projectile,
    @Nullable BlockPos blockPos,
    @Nullable BlockState blockState,
    @Nullable DamageSource source,
    @Nullable Vec3d position,
    @Nullable ItemStack stack,
    float damage,
    GemstoneQuality rarity) {
}
