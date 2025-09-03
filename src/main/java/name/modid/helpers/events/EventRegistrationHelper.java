package name.modid.helpers.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class EventRegistrationHelper {
  public static void initialize() {
    AttackEntityCallback.EVENT.register(EventStunned::setupEvent);
    AttackEntityCallback.EVENT.register(EventOnHitEffectModifiers::setupEvent);

    ServerTickEvents.END_SERVER_TICK.register(EventOnHit::setupEffect);
    ServerEntityEvents.ENTITY_LOAD.register(EventProjectileSpeed::setupEvent);
    PlayerBlockBreakEvents.AFTER.register(EventOnBlockBreak::setupEvent);
    PlayerBlockBreakEvents.BEFORE.register(EventOnBeforeBlockBreak::setupEvent);
    ServerLivingEntityEvents.AFTER_DAMAGE.register(EventOnDamage::setupEvent);
    ServerLivingEntityEvents.AFTER_DAMAGE.register(EventOnHit::setupEvent);
    ServerLivingEntityEvents.AFTER_DEATH.register(EventHarvestMark::setupEvent);
    ServerLivingEntityEvents.AFTER_DEATH.register(EventDetonate::setupEvent);

    ServerTickEvents.END_SERVER_TICK.register(server -> {
      for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
        EventAreaEffect.setupEvent(player);
      }
    });

    ServerTickEvents.END_WORLD_TICK.register(world -> {
      for (ServerPlayerEntity player : world.getPlayers()) {
        EventIncreaseSpawnrate.setupEvent(player);
      }
    });
  }
}
