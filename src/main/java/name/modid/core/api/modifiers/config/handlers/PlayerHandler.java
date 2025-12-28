package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.PlayerConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.EffectsRegistry;
import name.modid.core.network.OreVisionPayload;
import name.modid.core.utils.GetRandomBuff;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class PlayerHandler implements ModifierHandler<ModifierConfig.PlayerConfig> {
  private final Map<String, java.util.function.BiConsumer<List<GemstoneModifier>, ModifierContext>> handlers = Map.of(
      "PLAYER_WITHER_GUARD", this::handleWitherGuard,
      "PLAYER_PROJECTILE_IMMUNE", this::handleProjectileImmune,
      "PLAYER_RANDOM_EFFECT", this::handleRandomEffect,
      "PLAYER_SAVE_LETHAL", this::handleSaveLethal,
      "PLAYER_TICK_ORE_VISION", this::handleOreVision);

  private static final List<String> ORDER = List.of(
      "PLAYER_WITHER_GUARD",
      "PLAYER_PROJECTILE_IMMUNE",
      "PLAYER_RANDOM_EFFECT",
      "PLAYER_SAVE_LETHAL",
      "PLAYER_TICK_ORE_VISION");

  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty()) {
      return;
    }

    Map<String, List<GemstoneModifier>> grouped = modifiers.stream()
        .collect(Collectors.groupingBy(inst -> ((PlayerConfig) inst.getConfig()).eventType().getName()));

    for (String key : ORDER) {
      var group = grouped.get(key);
      if (group == null || group.isEmpty())
        continue;

      var fn = handlers.get(key);
      if (fn != null)
        fn.accept(group, ctx);
    }
  }

  private void handleOreVision(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof ServerPlayerEntity player)
        || !(ctx.getWorld() instanceof ServerWorld serverWorld)
        || player.age % 20 != 0) {
      return;
    }

    int RADIUS = 12;
    BlockPos ORIGIN_POS = player.getBlockPos();

    var valuableOres = List.of(
        net.minecraft.block.Blocks.DIAMOND_ORE,
        net.minecraft.block.Blocks.DEEPSLATE_DIAMOND_ORE,
        net.minecraft.block.Blocks.EMERALD_ORE,
        net.minecraft.block.Blocks.DEEPSLATE_EMERALD_ORE,
        net.minecraft.block.Blocks.ANCIENT_DEBRIS,
        net.minecraft.block.Blocks.GOLD_ORE,
        net.minecraft.block.Blocks.DEEPSLATE_GOLD_ORE,
        net.minecraft.block.Blocks.NETHER_GOLD_ORE);

    List<BlockPos> found = new ArrayList<>();

    for (int dx = -RADIUS; dx <= RADIUS; dx++) {
      for (int dy = -RADIUS; dy <= RADIUS; dy++) {
        for (int dz = -RADIUS; dz <= RADIUS; dz++) {
          BlockPos pos = ORIGIN_POS.add(dx, dy, dz);

          if (valuableOres.contains(serverWorld.getBlockState(pos).getBlock())) {
            found.add(pos);
          }
        }
      }
    }

    if (found.isEmpty()) {
      return;
    }

    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeVarInt(found.size());

    for (BlockPos pos : found) {
      buf.writeBlockPos(pos);
    }

    ServerPlayNetworking.send(player, new OreVisionPayload(found));
  }

  private void handleWitherGuard(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!modifiers.isEmpty()) {
      ctx.setActionResult(ActionResult.SUCCESS);
    } else {
      ctx.setActionResult(ActionResult.FAIL);
    }
  }

  private void handleProjectileImmune(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity owner)
        || !(ctx.getTarget() instanceof LivingEntity target)
        || ctx.getProjectile() == null
        // I don't remember why i need this here
        || !owner.getUuid().equals(target.getUuid())) {
      ctx.setIsHurtable(true);
      return;
    }

    float healthPercent = target.getHealth() / target.getMaxHealth();
    float capImmunePercent = (float) ModifierUtils.cappedProcChance(
        modifiers.stream()
            .map(m -> ((PlayerConfig) m.getConfig()).values().get(m.getRarityType()))
            .toList());

    if (healthPercent < capImmunePercent) {
      ctx.setIsHurtable(false);
    } else {
      ctx.setIsHurtable(true);
    }
  }

  private void handleRandomEffect(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity owner)
        || !(ctx.getTarget() instanceof LivingEntity target)
        // I don't remember why i need this here
        || !owner.getUuid().equals(target.getUuid())) {
      return;
    }

    int amplifier = ctx.getWorld().getRandom().nextInt(2);
    int duration = 0;
    double chance = 0.0;

    for (GemstoneModifier modifier : modifiers) {
      PlayerConfig config = (PlayerConfig) modifier.getConfig();
      duration += config.values().get(modifier.getRarityType());
      chance += config.additionalValues().get(modifier.getRarityType());
    }

    if (ModifierUtils.proc(ctx.getWorld(), chance)) {
      StatusEffectInstance buff = GetRandomBuff.positive(duration * 20, amplifier);
      owner.addStatusEffect(buff);
    }
  }

  private void handleSaveLethal(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity owner)
        || owner.isDead()
        || owner.isRemoved()
        || owner.isSpectator()
        || owner.hasStatusEffect(EffectsRegistry.LETHAL_WEAKNESS_EFFECT)) {
      return;
    }

    int amplifier = 1;
    int duration = 0;
    double healthThresholdBonus = 0.0;

    for (GemstoneModifier modifier : modifiers) {
      PlayerConfig config = (PlayerConfig) modifier.getConfig();
      duration += config.values().get(modifier.getRarityType());
      healthThresholdBonus += config.additionalValues().get(modifier.getRarityType());
    }

    float healthThreshold = (float) (0.0 + healthThresholdBonus);
    int buffDurationTicks = Math.max(0, duration) * 20;

    if (buffDurationTicks <= 0
        && owner.getHealth() > healthThreshold) {
      return;
    }

    owner.getWorld().playSound(
        null,
        owner.getBlockPos(),
        net.minecraft.sound.SoundEvents.ITEM_TOTEM_USE,
        net.minecraft.sound.SoundCategory.PLAYERS,
        1.0f,
        1.0f);

    owner.addStatusEffect(new StatusEffectInstance(
        StatusEffects.REGENERATION,
        buffDurationTicks,
        amplifier,
        false,
        true,
        true));

    owner.addStatusEffect(new StatusEffectInstance(
        StatusEffects.RESISTANCE,
        buffDurationTicks,
        0,
        false,
        true,
        true));

    owner.addStatusEffect(new StatusEffectInstance(
        EffectsRegistry.LETHAL_WEAKNESS_EFFECT,
        3 * 60 * 20,
        0,
        false,
        true,
        true));

    owner.addStatusEffect(new StatusEffectInstance(
        StatusEffects.ABSORPTION,
        buffDurationTicks,
        1,
        false,
        true,
        true));

    if (owner.getWorld() instanceof ServerWorld server) {
      double x = owner.getX();
      double y = owner.getBodyY(0.5);
      double z = owner.getZ();
      server.spawnParticles(
          net.minecraft.particle.ParticleTypes.TOTEM_OF_UNDYING,
          x, y, z,
          50,
          0.5, 0.8, 0.5,
          0.1);
      server.spawnParticles(
          net.minecraft.particle.ParticleTypes.GLOW,
          x, y, z,
          20,
          0.3, 0.6, 0.3,
          0.02);
    }
  }
}
