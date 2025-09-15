package name.modid.core.api.modifiers.impl.events;

import java.util.List;

import name.modid.core.api.modifiers.categories.ModifierOnBlockBreak;
import name.modid.core.api.modifiers.impl.EventType;
import name.modid.core.api.modifiers.impl.GemstoneModifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

public class BlockSmelter implements EventHandler<ModifierOnBlockBreak> {

  @Override
  public EventType type() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'type'");
  }

  @Override
  public void execute(ModifierOnBlockBreak ctx, List<? extends GemstoneModifier> mods, ServerWorld world,
      Random random) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'execute'");
  }

}
