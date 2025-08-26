package name.modid.config.datapack;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.type.ConditionType;
import name.modid.helpers.modifiers.type.EventType;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.util.Identifier;

public class ModifiersConfig {
  @SerializedName("gemstone_type")
  public GemstoneType gemstoneType;

  public Map<ModifierItemCategory, ModifierConfigEntry> modifiers;

  public enum ModifierType {
    @SerializedName("ON_HIT_EFFECT")
    ON_HIT_EFFECT,
    @SerializedName("AREA_EFFECT")
    AREA_EFFECT,
    @SerializedName("ON_BLOCK_BREAK")
    ON_BLOCK_BREAK,
    @SerializedName("MULTIPLY_ATTRIBUTE")
    MULTIPLY_ATTRIBUTE,
    @SerializedName("ATTRIBUTE")
    ATTRIBUTE,
    @SerializedName("ON_HIT")
    ON_HIT,
    @SerializedName("CUSTOM_CONDITION")
    CUSTOM_CONDITION,
    @SerializedName("ON_FIRST_HIT")
    ON_FIRST_HIT,
  }

  public static class ModifierConfigEntry {
    public ModifierType type;
  }

  public static class OnHitEffectConfig extends ModifierConfigEntry {
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

  public static class AreaEffectConfig extends ModifierConfigEntry {
    @SerializedName("radius_levels")
    public List<Double> radiusLevels;
    public int duration;
    public int amplifier;
    @SerializedName("effect_id")
    public Identifier effectId;
    @SerializedName("not_me")
    public Boolean notMe;
  }

  public static class OnBlockBreakConfig extends ModifierConfigEntry {
    @SerializedName("chance_levels")
    public List<Double> chanceLevels;
    @SerializedName("value_levels")
    public List<Double> valueLevels;
    @SerializedName("event_type")
    public EventType eventType;
  }

  public static class MultiplyAttributeConfig extends ModifierConfigEntry {
    @SerializedName("instances")
    public List<AttributeConfig> attributes;
  }

  public static class AttributeConfig extends ModifierConfigEntry {
    @SerializedName("attribute_id")
    public Identifier attributeId;
    @SerializedName("value_levels")
    public List<Double> valueLevels;
    public Operation operation;
  }

  public static class OnHitConfig extends ModifierConfigEntry {
    @SerializedName("chance_levels")
    public List<Double> chanceLevels;
    @SerializedName("event_type")
    public EventType eventType;
  }

  public static class OnFirstHitConfig extends ModifierConfigEntry {
    @SerializedName("value_levels")
    public List<Double> valueLevels;
    @SerializedName("event_type")
    public EventType eventType;
  }

  public static class CustomConditionConfig extends ModifierConfigEntry {
    @SerializedName("value_levels")
    public List<Double> valueLevels;
    @SerializedName("additional_value_levels")
    public List<Double> additionalValueLevels;
    @SerializedName("condition_type")
    public ConditionType conditionType;
  }
}
