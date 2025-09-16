package name.modid.core.api.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.modifiers.ModifierManagerLegacy;
import name.modid.core.api.modifiers.categories.ModifierOnHitEffectMelee;
import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class EventOnHitEffectModifiers {
  public static ActionResult setupEvent(PlayerEntity player, World world, Hand hand, Entity entity,
      EntityHitResult hitResult) {
    if (!world.isClient && entity instanceof LivingEntity target) {
      Item item = player.getStackInHand(hand).getItem();
      ItemStack itemStack = player.getStackInHand(hand);

      if (!GemstoneSlotHelper.isGemstonesExists(itemStack)) {
        return ActionResult.PASS;
      }

      GemstoneComponent[] gemstones = GemstoneSlotHelper.getGemstones(itemStack);
      Map<Integer, Map<GemstoneType, GemstoneQuality>> itemGemstones = new HashMap<>();

      for (int i = 0; i < gemstones.length; i++) {
        GemstoneComponent gem = gemstones[i];
        if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
          Map<GemstoneType, GemstoneQuality> m = new HashMap<>();
          m.put(gem.gemstoneType(), gem.GemstoneQualityType());
          itemGemstones.put(i, m);
        }
      }

      ArrayList<ModifierOnHitEffectMelee> gemstoneModifiers = ModifierGatheringHelper
          .getOnHitEffectModifiers(itemStack);
      ModifierManagerLegacy.applyOnHitEffectMeleeModifiers(gemstoneModifiers, item, itemStack,
          target, world);
    }
    return ActionResult.PASS;
  }
}
