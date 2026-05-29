package name.modid.core.content.registries;

import name.modid.core.content.events.CustomEvents;
import name.modid.core.content.events.handlers.EventAfterDeath;
import name.modid.core.content.events.handlers.EventAreaEffect;
import name.modid.core.content.events.handlers.EventMeleeEffect;
import name.modid.core.content.events.handlers.EventOnBeforeBlockBreak;
import name.modid.core.content.events.handlers.EventOnBlockBreak;
import name.modid.core.content.events.handlers.EventOnFirstHit;
import name.modid.core.content.events.handlers.EventOnFishing;
import name.modid.core.content.events.handlers.EventOnHitMelee;
import name.modid.core.content.events.handlers.EventOnHitProjectile;
import name.modid.core.content.events.handlers.EventOnMobDamage;
import name.modid.core.content.events.handlers.EventOnPlayerDamage;
import name.modid.core.content.events.handlers.EventOnPotionBrew;
import name.modid.core.content.events.handlers.EventPlayer;
import name.modid.core.content.events.handlers.PlayerRandomBuff;
import name.modid.core.content.events.loot.BlocksLootTable;
import name.modid.core.content.events.loot.ChestsLootTable;
import name.modid.core.content.events.loot.EntitiesLootTable;
import name.modid.core.content.events.misc.EventLastBrewer;
import name.modid.core.content.events.misc.EventProjectileSpeed;
import name.modid.core.content.events.misc.EventSparkSpawner;
import name.modid.core.content.events.misc.EventStunned;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class EventsRegistry {
  public static void initialize() {
    CustomEvents.ON_FIRST_HIT.register(EventOnFirstHit::setupEvent);
    CustomEvents.ON_FISHING.register(EventOnFishing::setupEvent);
    CustomEvents.ON_HIT_MELEE.register(EventOnHitMelee::setupEvent);
    CustomEvents.ON_HIT_PROJECTILE.register(EventOnHitProjectile::setupEvent);
    CustomEvents.ON_POTION_BREW.register(EventOnPotionBrew::setupEvent);

    // Effect related (Melee, Ranged and Area)
    ServerTickEvents.END_SERVER_TICK.register(server -> {
      for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
        EventAreaEffect.setupEvent(player);
      }
    });
    AttackEntityCallback.EVENT.register(EventMeleeEffect::setupEvent);
    ServerLivingEntityEvents.AFTER_DAMAGE.register(EventOnMobDamage::setup);
    ServerLivingEntityEvents.AFTER_DAMAGE.register(EventOnPlayerDamage::setup);

    // Block break
    PlayerBlockBreakEvents.AFTER.register(EventOnBlockBreak::setupEvent);
    PlayerBlockBreakEvents.BEFORE.register(EventOnBeforeBlockBreak::setupEvent);

    // TODO: rework
    // After death
    ServerLivingEntityEvents.AFTER_DEATH.register(EventAfterDeath::setupEvent);
    ServerLivingEntityEvents.AFTER_DEATH.register(PlayerRandomBuff::setupEvent);

    // Player (PLAYER_TICK_ events going in END_SERVER_TICK)
    ServerTickEvents.END_SERVER_TICK.register(server -> {
      for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
        EventPlayer.setupEventEndTick(player);
      }
    });
    ServerLivingEntityEvents.ALLOW_DAMAGE.register(EventPlayer::setupEvent);

    // Other
    AttackEntityCallback.EVENT.register(EventStunned::setupEvent);
    ServerEntityEvents.ENTITY_LOAD.register(EventProjectileSpeed::setup);
    UseBlockCallback.EVENT.register(EventLastBrewer::setup);
    ServerLivingEntityEvents.AFTER_DEATH.register(EventSparkSpawner::setup);

    LootTableEvents.MODIFY.register(BlocksLootTable::setup);
    LootTableEvents.MODIFY.register(EntitiesLootTable::setup);
    LootTableEvents.MODIFY.register(ChestsLootTable::setup);
  }
}
