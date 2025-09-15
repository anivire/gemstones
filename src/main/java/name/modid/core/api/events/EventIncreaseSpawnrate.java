package name.modid.core.api.events;

import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.utils.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings.SpawnEntry;

public class EventIncreaseSpawnrate {
  private static final int RADIUS = 32;
  private static final int MAX_MOBS_IN_RADIUS = 48;
  private static final int BASE_INTERVAL = 20;

  public static void setupEvent(ServerPlayerEntity player) {
    ServerWorld world = player.getServerWorld();
    double bonus = Utils.collectPlayerArmorValues(
        player,
        armorPiece -> ModifierGatheringHelper.getCustomConditionModifiers(armorPiece).stream()
            .filter(m -> m.getEventType() == EventType.INCREASE_MOB_SPAWNRATE)
            .map(m -> m.getValues().get(m.getRarityType()))
            .toList())
        .stream()
        .mapToDouble(Double::doubleValue)
        .sum();

    if (bonus <= 0) {
      return;
    }

    bonus = Math.min(bonus, 3.0);

    Random random = world.getRandom();
    long nearbyMobs = world.getEntitiesByClass(
        MobEntity.class,
        player.getBoundingBox().expand(RADIUS),
        e -> true).size();

    if (nearbyMobs >= MAX_MOBS_IN_RADIUS) {
      return;
    }

    int interval = (int) (BASE_INTERVAL / (1.0 + bonus));
    if (interval < 1) {
      interval = 1;
    }
    if (world.getTime() % interval != 0) {
      return;
    }

    int attempts = (int) (1 * (1.0 + bonus));
    for (int i = 0; i < attempts; i++) {
      trySpawnMob(world, player, random);
    }
  }

  private static void trySpawnMob(ServerWorld world, ServerPlayerEntity player, Random random) {
    int dx = random.nextBetween(-RADIUS, RADIUS);
    int dz = random.nextBetween(-RADIUS, RADIUS);

    BlockPos basePos = player.getBlockPos().add(dx, 0, dz);
    BlockPos spawnPos;

    if (random.nextBoolean()) {
      spawnPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, basePos);
    } else {
      int minY = world.getBottomY() + 5;
      int maxY = player.getBlockY() + 16;
      int y = random.nextBetween(minY, maxY);

      BlockPos candidate = new BlockPos(basePos.getX(), y, basePos.getZ());
      while (y > minY && !world.isAir(candidate)) {
        y--;
        candidate = candidate.down();
      }
      spawnPos = candidate;
    }

    if (!world.getWorldBorder().contains(spawnPos)) {
      return;
    }

    boolean isWater = !world.getFluidState(spawnPos).isEmpty();
    if (!isWater && !world.isAir(spawnPos)) {
      return;
    }

    RegistryEntry<Biome> biomeEntry = world.getBiome(spawnPos);
    SpawnGroup group;

    if (isWater) {
      SpawnGroup[] waterGroups = {
          SpawnGroup.WATER_AMBIENT,
          SpawnGroup.WATER_CREATURE,
          SpawnGroup.UNDERGROUND_WATER_CREATURE
      };
      group = waterGroups[random.nextInt(waterGroups.length)];
    } else {
      boolean isSurface = world.isSkyVisible(spawnPos);
      boolean isDay = world.isDay();
      group = isSurface
          ? (isDay ? SpawnGroup.CREATURE : SpawnGroup.MONSTER)
          : SpawnGroup.MONSTER;
    }

    Pool<SpawnEntry> pool = biomeEntry.value().getSpawnSettings().getSpawnEntries(group);

    if (!pool.isEmpty()) {
      SpawnEntry entry = pool.getOrEmpty(random).orElse(null);

      if (entry != null) {
        EntityType<?> type = entry.type;

        if (type.create(world) instanceof MobEntity mob) {
          if (SpawnRestriction.canSpawn(type, world, SpawnReason.NATURAL, spawnPos, random)) {
            mob.refreshPositionAndAngles(spawnPos, random.nextFloat() * 360F, 0F);
            world.spawnEntity(mob);
            world.spawnParticles(
                getSpawnParticle(group),
                mob.getX() + 0.5,
                mob.getY() + 0.5,
                mob.getZ() + 0.5,
                20,
                0.3,
                0.3,
                0.3,
                0.2);
            mob.addStatusEffect(new StatusEffectInstance(
                StatusEffects.GLOWING,
                200,
                0,
                false,
                false));
          }
        }
      }
    }
  }

  private static ParticleEffect getSpawnParticle(SpawnGroup group) {
    return switch (group) {
      case MONSTER -> ParticleTypes.CLOUD;
      case CREATURE -> ParticleTypes.HAPPY_VILLAGER;
      case WATER_CREATURE, WATER_AMBIENT, UNDERGROUND_WATER_CREATURE -> ParticleTypes.BUBBLE;
      default -> ParticleTypes.CLOUD;
    };
  }

  public static double getNearbyPlayersSpawnBonus(ServerWorld world, ChunkPos chunkPos) {
    return world.getPlayers().stream()
        .filter(p -> p.getBlockPos().isWithinDistance(chunkPos.getCenterAtY(RADIUS), RADIUS))
        .mapToDouble(player -> Utils.collectPlayerArmorValues(
            player,
            armorPiece -> ModifierGatheringHelper.getCustomConditionModifiers(armorPiece).stream()
                .filter(m -> m.getEventType() == EventType.INCREASE_MOB_SPAWNRATE)
                .map(m -> m.getValues().get(m.getRarityType()))
                .toList())
            .stream()
            .mapToDouble(Double::doubleValue)
            .sum())
        .sum();
  }
}