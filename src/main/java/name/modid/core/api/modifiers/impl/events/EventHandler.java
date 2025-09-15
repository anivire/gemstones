package name.modid.core.api.modifiers.impl.events;

import java.util.List;

import name.modid.core.api.modifiers.impl.EventType;
import name.modid.core.api.modifiers.impl.GemstoneModifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

public interface EventHandler<C> {
  EventType type();

  void execute(C ctx, List<? extends GemstoneModifier> mods, ServerWorld world, Random random);
}
