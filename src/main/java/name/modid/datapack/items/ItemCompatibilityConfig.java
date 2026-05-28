package name.modid.datapack.items;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import name.modid.core.api.modifiers.types.ModifierItemCategory;
import net.minecraft.util.Identifier;

public class ItemCompatibilityConfig {
  public List<Identifier> items = List.of();
  public List<Identifier> tags = List.of();
  public ModifierItemCategory category;
  @SerializedName("blacklist")
  public boolean blacklist = false;
}
