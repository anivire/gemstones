package name.modid.core.utils.saveLethal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import name.modid.Gemstones;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

// Cooldown of the Onyx lethal save. It is stored with the world instead of a status effect,
// so drinking milk or relogging does not reset it.
public class SaveLethalCooldownState extends PersistentState {
  private static final String ID = Gemstones.MOD_ID + "_save_lethal_cooldowns";
  private static final String COOLDOWNS_KEY = "cooldowns";

  private static final PersistentState.Type<SaveLethalCooldownState> TYPE = new PersistentState.Type<>(
      SaveLethalCooldownState::new,
      SaveLethalCooldownState::fromNbt,
      null);

  private final Map<UUID, Long> cooldownEnds = new HashMap<>();

  public static SaveLethalCooldownState get(MinecraftServer server) {
    return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE, ID);
  }

  public static boolean isOnCooldown(ServerWorld world, UUID playerId) {
    MinecraftServer server = world.getServer();
    Long end = get(server).cooldownEnds.get(playerId);
    return end != null && server.getOverworld().getTime() < end;
  }

  public static void startCooldown(ServerWorld world, UUID playerId, int durationTicks) {
    MinecraftServer server = world.getServer();
    SaveLethalCooldownState state = get(server);
    long now = server.getOverworld().getTime();

    state.cooldownEnds.entrySet().removeIf(entry -> entry.getValue() <= now);
    state.cooldownEnds.put(playerId, now + durationTicks);
    state.markDirty();
  }

  private static SaveLethalCooldownState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    SaveLethalCooldownState state = new SaveLethalCooldownState();
    NbtCompound cooldowns = nbt.getCompound(COOLDOWNS_KEY);

    for (String key : cooldowns.getKeys()) {
      try {
        state.cooldownEnds.put(UUID.fromString(key), cooldowns.getLong(key));
      } catch (IllegalArgumentException ignored) {
      }
    }

    return state;
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    NbtCompound cooldowns = new NbtCompound();
    cooldownEnds.forEach((playerId, end) -> cooldowns.putLong(playerId.toString(), end));
    nbt.put(COOLDOWNS_KEY, cooldowns);
    return nbt;
  }
}
