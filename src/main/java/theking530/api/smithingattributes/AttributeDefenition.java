package theking530.api.smithingattributes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public abstract class AttributeDefenition {
	protected final ResourceLocation id;
	protected final String unlocalizedName;
	protected final String type;
	protected final TextFormatting color;
	protected final List<AbstractAttributeModifier> modifiers;

	/**
	 * @param id
	 * @param unlocalizedName
	 * @param type
	 * @param color
	 */
	public AttributeDefenition(ResourceLocation id, String unlocalizedName, String type, TextFormatting color) {
		super();
		this.id = id;
		this.unlocalizedName = unlocalizedName;
		this.type = type;
		this.color = color;
		this.modifiers = new ArrayList<AbstractAttributeModifier>();
	}

	public ResourceLocation getId() {
		return id;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public String getType() {
		return type;
	}

	public TextFormatting getColor() {
		return color;
	}
}
