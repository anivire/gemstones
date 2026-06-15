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
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import name.modid.core.network.OreVisionPayload;
import name.modid.core.utils.GetRandomBuff;
import name.modid.core.utils.oreVision.OreVisionRadius;
import dev.architectury.networking.NetworkManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.SpawnSettings.SpawnEntry;

public class PlayerHandler implements ModifierHandler<ModifierConfig.PlayerConfig> {
  enum SpawnEnviroment {
    GROUND, WATER, LAVA
  }

  private final Map<String, java.util.function.BiConsumer<List<GemstoneModifier>, ModifierContext>> handlers = Map.of(

      "PLAYER_RANDOM_EFFECT", this::handleRandomEffect,
      "PLAYER_WITHER_GUARD", this::handleWitherGuard,
      "PLAYER_PROJECTILE_IMMUNE", this::handleProjectileImmune,
      "PLAYER_TICK_INCREASE_MOB_SPAWNRATE", this::handleIncreaseSpawnrate,
      "PLAYER_TICK_ORE_VISION", this::handleOreVision);

  private static final List<String> ORDER = List.of(
      "PLAYER_RANDOM_EFFECT",
      "PLAYER_WITHER_GUARD",
      "PLAYER_PROJECTILE_IMMUNE",
      "PLAYER_TICK_INCREASE_MOB_SPAWNRATE",
      "PLAYER_TICK_ORE_VISION");

  @Override
  public boolean supports(GemstoneModifier modifier) {
    PlayerConfig config = (PlayerConfig) modifier.getConfig();
    return switch (config.eventType().getName()) {
      case "PLAYER_TICK_ORE_VISION" -> modifier.getItemCategory() == ModifierItemCategory.TOOLS;
      case "PLAYER_RANDOM_EFFECT",
          "PLAYER_WITHER_GUARD",
          "PLAYER_PROJECTILE_IMMUNE",
          "PLAYER_TICK_INCREASE_MOB_SPAWNRATE" ->
        modifier.getItemCategory() == ModifierItemCategory.ARMOR;
      default -> false;
    };
  }

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

    int radius = getOreVisionRadius(modifiers);
    BlockPos ORIGIN_POS = player.getBlockPos();

    Map<Block, Integer> valuableOres = Map.ofEntries(
        Map.entry(Blocks.DIAMOND_ORE, 0x2EE0FF),
        Map.entry(Blocks.DEEPSLATE_DIAMOND_ORE, 0x2EE0FF),
        Map.entry(Blocks.EMERALD_ORE, 0x2EFF35),
        Map.entry(Blocks.DEEPSLATE_EMERALD_ORE, 0x2EFF35),
        Map.entry(Blocks.ANCIENT_DEBRIS, 0xB06A4F),
        Map.entry(Blocks.GOLD_ORE, 0xFFF52E),
        Map.entry(Blocks.DEEPSLATE_GOLD_ORE, 0xFFF52E),
        Map.entry(Blocks.NETHER_GOLD_ORE, 0xFFF52E),
        Map.entry(Blocks.IRON_ORE, 0xFFD1BD),
        Map.entry(Blocks.DEEPSLATE_IRON_ORE, 0xFFD1BD),
        Map.entry(Blocks.COPPER_ORE, 0xEB5E34),
        Map.entry(Blocks.DEEPSLATE_COPPER_ORE, 0xEB5E34),
        Map.entry(Blocks.COAL_ORE, 0x505050),
        Map.entry(Blocks.DEEPSLATE_COAL_ORE, 0x505050),
        Map.entry(Blocks.REDSTONE_ORE, 0xFF2E2E),
        Map.entry(Blocks.DEEPSLATE_REDSTONE_ORE, 0xFF2E2E),
        Map.entry(Blocks.LAPIS_ORE, 0x312EFF),
        Map.entry(Blocks.DEEPSLATE_LAPIS_ORE, 0x312EFF),
        Map.entry(Blocks.NETHER_QUARTZ_ORE, 0xFFFFFF));

    List<OreVisionPayload.HighlightedOre> found = new ArrayList<>();

    for (int dx = -radius; dx <= radius; dx++) {
      for (int dy = -radius; dy <= radius; dy++) {
        for (int dz = -radius; dz <= radius; dz++) {
          BlockPos pos = ORIGIN_POS.add(dx, dy, dz);

          Integer color = valuableOres.get(serverWorld.getBlockState(pos).getBlock());
          if (color != null) {
            found.add(new OreVisionPayload.HighlightedOre(pos.toImmutable(), color));
          }
        }
      }
    }

    if (found.isEmpty()) {
      return;
    }

    NetworkManager.sendToPlayer(player, new OreVisionPayload(found));
  }

  private int getOreVisionRadius(List<GemstoneModifier> modifiers) {
    double radius = 0.0;
    for (GemstoneModifier modifier : modifiers) {
      PlayerConfig config = (PlayerConfig) modifier.getConfig();
      radius = Math.max(radius, config.values().get(modifier.getRarityType()));
    }

    return OreVisionRadius.fromValue(radius);
  }

  // TODO: structures pool
  private void handleIncreaseSpawnrate(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    final int SPAWN_COOLDOWN = 20;

    if (!(ctx.getOwner() instanceof ServerPlayerEntity player)
        || !(ctx.getWorld() instanceof ServerWorld world)
        || player.age % (SPAWN_COOLDOWN * 20) != 0
        || player.isSpectator()) {
      return;
    }

    final int DEFAULT_RADIUS = 12;
    final int DEFAULT_SPAWN_ATTEMPTS = 3;
    final int DEFAULT_SPAWN_RETRIES = 2;

    boolean spawnedAny = false;
    BlockPos origin = player.getBlockPos();
    Biome biome = world.getBiome(origin).value();
    Pool<SpawnEntry> spawnPool = biome.getSpawnSettings()
        .getSpawnEntries(SpawnGroup.MONSTER);

    if (spawnPool == null) {
      return;
    }

    Random random = world.getRandom();
    boolean playerInWater = world.getFluidState(player.getBlockPos()).isIn(FluidTags.WATER);
    boolean playerInLava = world.getFluidState(player.getBlockPos()).isIn(FluidTags.LAVA);

    double increasedSpawnPercent = 0.0;
    int totalGemsCopies = 0;

    for (GemstoneModifier modifier : modifiers) {
      PlayerConfig config = (PlayerConfig) modifier.getConfig();
      increasedSpawnPercent += config.values().get(modifier.getRarityType());
      totalGemsCopies++;
    }

    for (int i = 0; i < DEFAULT_SPAWN_ATTEMPTS + totalGemsCopies; i++) {
      SpawnEntry entry = spawnPool.getOrEmpty(random).orElse(null);

      if (entry == null) {
        continue;
      }

      int groupSize = random.nextBetween(entry.minGroupSize, entry.maxGroupSize);
      int triesCount = (int) Math.round(
          DEFAULT_SPAWN_RETRIES * (1.0f + increasedSpawnPercent));

      if (random.nextDouble() < increasedSpawnPercent % 1.0) {
        triesCount++;
      }

      for (int j = 0; j < groupSize; j++) {
        boolean spawned = false;

        for (int tries = 0; tries < triesCount && !spawned; tries++) {
          BlockPos pos = origin.add(
              random.nextBetween(-DEFAULT_RADIUS, DEFAULT_RADIUS),
              random.nextBetween(-4, 4),
              random.nextBetween(-DEFAULT_RADIUS, DEFAULT_RADIUS));

          SpawnEnviroment env = playerInWater ? SpawnEnviroment.WATER
              : playerInLava ? SpawnEnviroment.LAVA
                  : SpawnEnviroment.GROUND;

          // Tweak spawn by adding mobs other pools
          boolean valid = switch (env) {
            case WATER -> isWaterMob(entry.type) && isValidWaterSpawn(world, pos);
            case LAVA -> isLavaMob(entry.type) && isValidLavaSpawn(world, pos);
            case GROUND -> isValidGroundSpawn(world, pos);
          };

          if (!valid) {
            continue;
          }

          // Additional spawn rules
          // Cap spawrate by 50 mob entities
          if (world.getEntitiesByClass(
              MobEntity.class,
              player.getBoundingBox().expand(DEFAULT_RADIUS),
              e -> true).size() > 40) {
            return;
          }

          // Spawn slimes only in swamp
          if (!(world.getBiome(origin).matchesKey(BiomeKeys.SWAMP)
              || world.getBiome(origin).matchesKey(BiomeKeys.MANGROVE_SWAMP))
              && entry.type == EntityType.SLIME) {
            continue;
          }

          // Reduce ghasts spawn
          if (entry.type == EntityType.GHAST) {
            if (random.nextFloat() > 0.25f) {
              continue;
            }

            groupSize = 1;
            triesCount = Math.max(1, triesCount / 3);
          }

          Entity entity = entry.type.spawn(world, pos, SpawnReason.SPAWNER);

          if (entity != null && entity instanceof LivingEntity) {
            spawned = true;
            spawnedAny = true;

            world.spawnParticles(
                ParticleTypes.LARGE_SMOKE,
                entity.getX(),
                entity.getY() + 1,
                entity.getZ(),
                20,
                0.5,
                0.5,
                0.5,
                0.02);
            world.spawnParticles(
                ParticleTypes.FLAME,
                entity.getX(),
                entity.getY() + 1,
                entity.getZ(),
                20,
                0.5,
                0.5,
                0.5,
                0.02);
          }
        }
      }
    }

    if (spawnedAny) {
      world.spawnParticles(
          ParticleTypes.LARGE_SMOKE,
          player.getX(),
          player.getY() + 1,
          player.getZ(),
          10,
          0.5,
          0.5,
          0.5,
          0.02);
      world.spawnParticles(
          ParticleTypes.FLAME,
          player.getX(),
          player.getY() + 1,
          player.getZ(),
          20,
          0.5,
          0.5,
          0.5,
          0.02);
    }
  }

  private boolean isValidGroundSpawn(ServerWorld world, BlockPos pos) {
    return world.isAir(pos)
        && world.isAir(pos.up())
        && world.getBlockState(pos.down())
            .isOpaqueFullCube(world, pos.down());
  }

  private boolean isValidWaterSpawn(ServerWorld world, BlockPos pos) {
    return world.getFluidState(pos).isIn(FluidTags.WATER)
        && world.getFluidState(pos.up()).isIn(FluidTags.WATER);
  }

  private boolean isValidLavaSpawn(ServerWorld world, BlockPos pos) {
    return world.getFluidState(pos).isIn(FluidTags.LAVA)
        && world.getFluidState(pos.up()).isIn(FluidTags.LAVA);
  }

  private boolean isWaterMob(EntityType<?> type) {
    return type == EntityType.DROWNED
        || type == EntityType.GUARDIAN
        || type == EntityType.ELDER_GUARDIAN;
  }

  private boolean isLavaMob(EntityType<?> type) {
    return type == EntityType.STRIDER;
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
        || ctx.getProjectile() == null) {
      ctx.setIsHurtable(true);
      return;
    }

    float healthPercent = owner.getHealth() / owner.getMaxHealth();
    float capImmunePercent = Math.min(1.0f, (float) modifiers.stream()
        .mapToDouble(m -> Math.abs(((PlayerConfig) m.getConfig()).values().get(m.getRarityType())))
        .sum());

    if (healthPercent < capImmunePercent) {
      ctx.setIsHurtable(false);
    } else {
      ctx.setIsHurtable(true);
    }
  }

  private void handleRandomEffect(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity owner)
        || !(ctx.getTarget() instanceof LivingEntity target)
        || owner.getUuid().equals(target.getUuid())) {
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

    if (ModifierUtils.proc(ctx.getWorld(), chance)
        && !target.isAlive()) {
      StatusEffectInstance buff = GetRandomBuff.positive(duration * 20, amplifier);
      owner.addStatusEffect(buff);
    }
  }
}
