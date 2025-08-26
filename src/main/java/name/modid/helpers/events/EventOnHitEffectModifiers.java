package name.modid.helpers.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneSocketingHelper;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.components.Gemstone;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.category.ModifierOnHitEffect;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
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

      if (!GemstoneSocketingHelper.isGemstonesExists(itemStack)) {
        return ActionResult.PASS;
      }

      Gemstone[] gemstones = GemstoneSocketingHelper.getGemstones(itemStack);
      Map<Integer, Map<GemstoneType, GemstoneRarity>> itemGemstones = new HashMap<>();

      for (int i = 0; i < gemstones.length; i++) {
        Gemstone gem = gemstones[i];
        if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
          Map<GemstoneType, GemstoneRarity> m = new HashMap<>();
          m.put(gem.gemstoneType(), gem.gemstoneRarityType());
          itemGemstones.put(i, m);
        }
      }

      ArrayList<ModifierOnHitEffect> gemstoneModifiers = new ArrayList<>();
      for (Map.Entry<Integer, Map<GemstoneType, GemstoneRarity>> m : itemGemstones.entrySet()) {
        Map<GemstoneType, GemstoneRarity> i = m.getValue();
        for (Map.Entry<GemstoneType, GemstoneRarity> e : i.entrySet()) {
          GemstoneType gemstoneType = e.getKey();
          GemstoneRarity gemstoneRarity = e.getValue();
          GemstoneModifier gemstoneModifier = ModifierHelper.getGemstoneModifierForItem(gemstoneType, item);
          if (gemstoneModifier != null
              && gemstoneModifier instanceof ModifierOnHitEffect modifierOnHitEffect) {
            ModifierOnHitEffect newModifier = new ModifierOnHitEffect(
                modifierOnHitEffect.inflitChance, modifierOnHitEffect.duration,
                modifierOnHitEffect.amplifier, modifierOnHitEffect.itemType,
                modifierOnHitEffect.effect, modifierOnHitEffect.isStacking,
                modifierOnHitEffect.maxStackCount, modifierOnHitEffect.gemstoneType);
            newModifier.setRarityType(gemstoneRarity);
            gemstoneModifiers.add(newModifier);
          }
        }
      }

      GemstoneSocketingHelper.applyOnHitEffectModifiers(gemstoneModifiers, item, itemStack,
          target, world);
    }
    return ActionResult.PASS;
  }
}
