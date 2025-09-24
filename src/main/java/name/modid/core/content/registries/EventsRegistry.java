package name.modid.core.content.registries;

import name.modid.core.content.attributes.EventStunned;
import name.modid.core.content.attributes.ProjectileSpeed;
import name.modid.core.content.events.EventAfterDeath;
import name.modid.core.content.events.EventAreaEffect;
import name.modid.core.content.events.EventMeleeEffect;
import name.modid.core.content.events.EventOnBeforeBlockBreak;
import name.modid.core.content.events.EventOnBlockBreak;
import name.modid.core.content.events.EventOnHitMelee;
import name.modid.core.content.events.EventOnHitProjectile;
import name.modid.core.content.events.EventProjectileEffect;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class EventsRegistry {
  public static void initialize() {
    // TODO: fix spaming proc for all modifiers which related on hit events

    // Effect related
    ServerTickEvents.END_SERVER_TICK.register(server -> {
      for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
        EventAreaEffect.setupEvent(player);
      }
    });
    AttackEntityCallback.EVENT.register(EventMeleeEffect::setupEvent);
    ServerLivingEntityEvents.AFTER_DAMAGE.register(EventProjectileEffect::setupEvent);

    // Block break
    PlayerBlockBreakEvents.AFTER.register(EventOnBlockBreak::setupEvent);
    PlayerBlockBreakEvents.BEFORE.register(EventOnBeforeBlockBreak::setupEvent);

    // On hit
    ServerLivingEntityEvents.AFTER_DAMAGE.register(EventOnHitProjectile::setupEvent);
    ServerLivingEntityEvents.AFTER_DAMAGE.register(EventOnHitMelee::setupEvent);

    ServerLivingEntityEvents.AFTER_DEATH.register(EventAfterDeath::setupEvent);

    AttackEntityCallback.EVENT.register(EventStunned::setupEvent);
    ServerEntityEvents.ENTITY_LOAD.register(ProjectileSpeed::setup);
  }
}
