package name.modid.config.datapack;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import name.modid.helpers.modifiers.ModifierItemCaregory;
import name.modid.helpers.modifiers.modifierTypes.EventType;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.util.Identifier;

public class ModifiersConfig {
  @SerializedName("gemstone_type")
  public GemstoneType gemstoneType;

  public Map<ModifierItemCaregory, ModifierConfigEntry> modifiers;

  public enum ModifierType {
    @SerializedName("ON_HIT_EFFECT")
    ON_HIT_EFFECT,
    @SerializedName("ON_BLOCK_BREAK")
    ON_BLOCK_BREAK,
    @SerializedName("MULTIPLY_ATTRIBUTE")
    MULTIPLY_ATTRIBUTE
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
    public List<AttributeModifierConfig> attributes;
  }

  public static class AttributeModifierConfig {
    @SerializedName("attribute_id")
    public Identifier attributeId;
    @SerializedName("value_levels")
    public List<Double> valueLevels;
    public Operation operation;
  }
}
