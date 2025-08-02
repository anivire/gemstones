package name.modid.helpers.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import name.modid.helpers.ItemGemstoneHelper;
import name.modid.helpers.components.Gemstone;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.GemstoneModifierHelper;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.types.GemstoneRarityType;
import name.modid.helpers.types.GemstoneType;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class EventRegistrationHelper {
  public static void initialize() {
    /*
     * Register only MELEE onHitEffect modifiers
     */
    AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
      if (!world.isClient && entity instanceof LivingEntity target) {
        Item item = player.getStackInHand(hand).getItem();
        ItemStack itemStack = player.getStackInHand(hand);

        if (!ItemGemstoneHelper.isGemstonesExists(itemStack)) {
          return ActionResult.PASS;
        }

        Gemstone[] gemstones = ItemGemstoneHelper.getGemstones(itemStack);
        Map<Integer, Map<GemstoneType, GemstoneRarityType>> itemGemstones = new HashMap<>();

        for (int i = 0; i < gemstones.length; i++) {
          Gemstone gem = gemstones[i];
          if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
            Map<GemstoneType, GemstoneRarityType> m = new HashMap<>();
            m.put(gem.gemstoneType(), gem.gemstoneRarityType());
            itemGemstones.put(i, m);
          }
        }

        ArrayList<ModifierOnHitEffect> gemstoneModifiers = new ArrayList<>();
        for (Map.Entry<Integer, Map<GemstoneType, GemstoneRarityType>> m : itemGemstones.entrySet()) {
          Map<GemstoneType, GemstoneRarityType> i = m.getValue();

          for (Map.Entry<GemstoneType, GemstoneRarityType> e : i.entrySet()) {
            GemstoneType gemstoneType = e.getKey();
            GemstoneRarityType gemstoneRarity = e.getValue();
            GemstoneModifier gemstoneModifier = GemstoneModifierHelper.getGemstoneModifierForItem(gemstoneType, item);

            if (gemstoneModifier != null) {
              if (gemstoneModifier instanceof ModifierOnHitEffect modifierOnHitEffect) {
                ModifierOnHitEffect newModifier = new ModifierOnHitEffect(modifierOnHitEffect.inflitChance,
                    modifierOnHitEffect.duration, modifierOnHitEffect.amplifier, modifierOnHitEffect.itemType,
                    modifierOnHitEffect.effect, modifierOnHitEffect.isStacking, modifierOnHitEffect.maxStackCount,
                    modifierOnHitEffect.gemstoneType);
                newModifier.setRarityType(gemstoneRarity);
                gemstoneModifiers.add(newModifier);
              }
            }
          }
        }

        ItemGemstoneHelper.applyOnHitEffectModifiers(gemstoneModifiers, item, itemStack, target, world);
      }

      return ActionResult.PASS;
    });
  }
}
