package name.modid.datapack.modifiers;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.util.Identifier;

public class ModifiersRawConfig {
  @SerializedName("gemstone_type")
  public GemstoneType gemstoneType;

  public Map<ModifierItemCategory, ModifierRawConfigEntry> modifiers;

  public enum ModifierType {
    @SerializedName("AREA_EFFECT")
    AREA_EFFECT,
    @SerializedName("ON_BLOCK_BREAK")
    ON_BLOCK_BREAK,
    @SerializedName("ATTRIBUTE")
    ATTRIBUTE,
    @SerializedName("MULTIPLY_ATTRIBUTE")
    MULTIPLY_ATTRIBUTE,
    @SerializedName("CUSTOM_CONDITION")
    CUSTOM_CONDITION,
    @SerializedName("ON_HIT")
    ON_HIT,
    @SerializedName("ON_HIT_EFFECT")
    ON_HIT_EFFECT,
    @SerializedName("ON_FIRST_HIT")
    ON_FIRST_HIT,
  }

  public static class ModifierRawConfigEntry {
    public ModifierType type;
  }

  public static class OnHitEffectConfig extends ModifierRawConfigEntry {
    @SerializedName("chance_levels")
    public List<Double> chanceLevels;
    public int duration;
    public int amplifier;
    @SerializedName("effect_id")
    public Identifier effectId;
    @SerializedName("is_stacking")
    public Boolean isStacking;
    @SerializedName("max_stack_count")
    public Integer maxStackCount;
  }

  public static class AreaEffectConfig extends ModifierRawConfigEntry {
    @SerializedName("radius_levels")
    public List<Double> radiusLevels;
    public int duration;
    public int amplifier;
    @SerializedName("effect_id")
    public Identifier effectId;
    @SerializedName("not_me")
    public Boolean notMe;
    @SerializedName("only_players")
    public Boolean onlyPlayers;
  }

  public static class OnBlockBreakConfig extends ModifierRawConfigEntry {
    @SerializedName("chance_levels")
    public List<Double> chanceLevels;
    @SerializedName("value_levels")
    public List<Double> valueLevels;
    @SerializedName("event_type")
    public EventType eventType;
  }

  public static class MultiplyAttributeConfig extends ModifierRawConfigEntry {
    @SerializedName("instances")
    public List<AttributeConfig> attributes;
  }

  public static class AttributeConfig extends ModifierRawConfigEntry {
    @SerializedName("attribute_id")
    public Identifier attributeId;
    @SerializedName("value_levels")
    public List<Double> valueLevels;
    public Operation operation;
  }

  public static class OnHitConfig extends ModifierRawConfigEntry {
    @SerializedName("chance_levels")
    public List<Double> chanceLevels;
    @SerializedName("event_type")
    public EventType eventType;
  }

  public static class OnFirstHitConfig extends ModifierRawConfigEntry {
    @SerializedName("value_levels")
    public List<Double> valueLevels;
    @SerializedName("event_type")
    public EventType eventType;
  }

  public static class CustomConditionConfig extends ModifierRawConfigEntry {
    @SerializedName("value_levels")
    public List<Double> valueLevels;
    @SerializedName("additional_value_levels")
    public List<Double> additionalValueLevels;
    @SerializedName("event_type")
    public EventType eventType;
  }
}
