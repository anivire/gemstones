package name.modid.core.content.events.misc;

import org.jetbrains.annotations.Nullable;

import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class EventStunned {
  public static ActionResult setupEvent(PlayerEntity player, World world, Hand hand, Entity entity,
      @Nullable EntityHitResult hitResult) {
    if (player.hasStatusEffect(EffectsRegistry.stunnedEntry())) {
      return ActionResult.FAIL;
    }

    return ActionResult.PASS;
  }
}
