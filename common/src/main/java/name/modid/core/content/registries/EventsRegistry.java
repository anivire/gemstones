package name.modid.core.content.registries;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.LootEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import name.modid.core.api.modifiers.config.handlers.BlockBreakHandler;
import name.modid.core.content.events.CustomEvents;
import name.modid.core.content.events.handlers.EventAfterDeath;
import name.modid.core.content.events.handlers.EventAreaEffect;
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
import name.modid.core.network.NetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public class EventsRegistry {
  public static void initialize() {
    CustomEvents.ON_FIRST_HIT.register(EventOnFirstHit::setupEvent);
    CustomEvents.ON_FISHING.register(EventOnFishing::setupEvent);
    CustomEvents.ON_HIT_MELEE.register(EventOnHitMelee::setupEvent);
    CustomEvents.ON_HIT_PROJECTILE.register(EventOnHitProjectile::setupEvent);
    CustomEvents.ON_POTION_BREW.register(EventOnPotionBrew::setupEvent);

    // Effect related (Melee, Ranged and Area)
    TickEvent.SERVER_POST.register(server -> {
      BlockBreakHandler.tickPendingRegenerations();
      for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
        EventAreaEffect.setupEvent(player);
      }
    });
    EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
      if (!EventPlayer.setupEvent(entity, source, amount)) {
        return EventResult.interruptFalse();
      }

      EventOnMobDamage.setup(entity, source, amount, amount, false);
      if (!EventOnPlayerDamage.setup(entity, source, amount, amount, false)) {
        return EventResult.interruptFalse();
      }
      return EventResult.pass();
    });

    // Block break
    BlockEvent.BREAK.register((world, pos, state, player, xp) -> {
      boolean canBreak = EventOnBeforeBlockBreak.setupEvent(world, player, pos, state, world.getBlockEntity(pos));
      if (!canBreak) {
        return EventResult.interruptFalse();
      }

      EventOnBlockBreak.setupEvent(world, player, pos, state, world.getBlockEntity(pos));
      return EventResult.pass();
    });

    // TODO: rework
    // After death
    EntityEvent.LIVING_DEATH.register((entity, source) -> {
      EventAfterDeath.setupEvent(entity, source);
      PlayerRandomBuff.setupEvent(entity, source);
      EventSparkSpawner.setup(entity, source);
      return EventResult.pass();
    });

    // Player (PLAYER_TICK_ events going in END_SERVER_TICK)
    TickEvent.PLAYER_POST.register(player -> {
      if (player instanceof ServerPlayerEntity serverPlayer) {
        EventPlayer.setupEventEndTick(serverPlayer);
      }
    });
    PlayerEvent.PLAYER_JOIN.register(NetworkHandler::sendDatapackSync);

    // Other
    PlayerEvent.ATTACK_ENTITY.register((player, world, target, hand, hitResult) ->
        toEventResult(EventStunned.setupEvent(player, world, hand, target, hitResult)));
    EntityEvent.ADD.register((entity, world) -> {
      if (world instanceof ServerWorld serverWorld) {
        EventProjectileSpeed.setup(entity, serverWorld);
      }
      return EventResult.pass();
    });
    InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) ->
        toEventResult(EventLastBrewer.setup(player, player.getWorld(), hand, pos)));

    LootEvent.MODIFY_LOOT_TABLE.register(BlocksLootTable::setup);
    LootEvent.MODIFY_LOOT_TABLE.register(EntitiesLootTable::setup);
    LootEvent.MODIFY_LOOT_TABLE.register(ChestsLootTable::setup);
  }

  private static EventResult toEventResult(ActionResult result) {
    return switch (result) {
      case SUCCESS, SUCCESS_NO_ITEM_USED, CONSUME -> EventResult.interruptTrue();
      case FAIL -> EventResult.interruptFalse();
      default -> EventResult.pass();
    };
  }
}
