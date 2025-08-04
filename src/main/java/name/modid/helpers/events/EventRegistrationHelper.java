package name.modid.helpers.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import name.modid.helpers.ItemGemstoneHelper;
import name.modid.helpers.components.ComponentsHelper;
import name.modid.helpers.components.ExtraHearts;
import name.modid.helpers.components.Gemstone;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.GemstoneModifierHelper;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.types.GemstoneRarityType;
import name.modid.helpers.types.GemstoneType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class EventRegistrationHelper {
  public static void initialize() {
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
        for (Map.Entry<Integer, Map<GemstoneType, GemstoneRarityType>> m : itemGemstones
            .entrySet()) {
          Map<GemstoneType, GemstoneRarityType> i = m.getValue();
          for (Map.Entry<GemstoneType, GemstoneRarityType> e : i.entrySet()) {
            GemstoneType gemstoneType = e.getKey();
            GemstoneRarityType gemstoneRarity = e.getValue();
            GemstoneModifier gemstoneModifier =
                GemstoneModifierHelper.getGemstoneModifierForItem(gemstoneType, item);
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

        ItemGemstoneHelper.applyOnHitEffectModifiers(gemstoneModifiers, item, itemStack, target,
            world);
      }
      return ActionResult.PASS;
    });

    ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
      PlayerEntity player = handler.getPlayer();
      ExtraHearts extraHearts = player.getAttached(ComponentsHelper.EXTRA_HEARTS);
      if (extraHearts == null) {
        extraHearts = new ExtraHearts(0);
        player.setAttached(ComponentsHelper.EXTRA_HEARTS, extraHearts);
      }
      extraHearts.apply(player);
    });

    PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
      ExtraHearts currentHearts = player.getAttached(ComponentsHelper.EXTRA_HEARTS);
      if (currentHearts == null) {
        currentHearts = new ExtraHearts(0);
      }
      ExtraHearts newHearts = new ExtraHearts(currentHearts.value() + 1);
      player.setAttached(ComponentsHelper.EXTRA_HEARTS, newHearts);
      newHearts.apply(player);
    });

    ServerLivingEntityEvents.AFTER_DAMAGE
        .register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
          float absorption = entity.getAbsorptionAmount();
          float remainingDamage = damageTaken;

          if (absorption > 0) {
            float absorbedByArmor = Math.min(absorption, remainingDamage);
            entity.setAbsorptionAmount(absorption - absorbedByArmor);
            remainingDamage -= absorbedByArmor;

            if (remainingDamage <= 0) {
              return;
            }
          }

          ExtraHearts extraHearts = entity.getAttached(ComponentsHelper.EXTRA_HEARTS);
          if (extraHearts != null && extraHearts.value() > 0) {
            float customHealth = (float) extraHearts.value() * 2.0f;
            int heartsToRemove = (int) Math.ceil(damageTaken / 2.0);
            extraHearts.reduceHearts(entity, heartsToRemove);

            if (customHealth >= damageTaken) {
              entity.setHealth(entity.getHealth() + damageTaken);
            } else {
              entity.setHealth(entity.getHealth() + customHealth);
            }
          }
        });
  }
}
