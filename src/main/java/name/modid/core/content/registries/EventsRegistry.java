package name.modid.core.content.registries;

public class EventsRegistry {
  public static void initialize() {
    // TODO: fix spaming proc for all modifiers which related on hit events

    // AttackEntityCallback.EVENT.register(EventStunned::setupEvent);
    // AttackEntityCallback.EVENT.register(EventOnHitEffectModifiers::setupEvent);

    // ServerEntityEvents.ENTITY_LOAD.register(EventProjectileSpeed::setupEvent);
    // PlayerBlockBreakEvents.AFTER.register(EventOnBlockBreak::setupEvent);
    // PlayerBlockBreakEvents.BEFORE.register(EventOnBeforeBlockBreak::setupEvent);
    // ServerLivingEntityEvents.AFTER_DAMAGE.register(EventOnDamage::setupEvent);
    // ServerLivingEntityEvents.AFTER_DAMAGE.register(EventOnHitProjectile::setupEvent);
    // ServerLivingEntityEvents.AFTER_DAMAGE.register(EventOnHitMelee::setupEvent);
    // ServerLivingEntityEvents.AFTER_DEATH.register(EventHarvestMark::setupEvent);
    // ServerLivingEntityEvents.AFTER_DEATH.register(EventDetonate::setupEvent);

    // ServerTickEvents.END_SERVER_TICK.register(server -> {
    // for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
    // EventAreaEffect.setupEvent(player);
    // }
    // });

    // ServerTickEvents.END_WORLD_TICK.register(world -> {
    // for (ServerPlayerEntity player : world.getPlayers()) {
    // EventIncreaseSpawnrate.setupEvent(player);
    // }
    // });
  }
}
