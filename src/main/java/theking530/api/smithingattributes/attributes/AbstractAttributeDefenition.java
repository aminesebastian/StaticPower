package theking530.api.smithingattributes.attributes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.smithingattributes.attributes.modifiers.AbstractAttributeModifier;

public abstract class AbstractAttributeDefenition<T, K extends AbstractAttributeModifier<T>> implements INBTSerializable<CompoundNBT> {
	protected final ResourceLocation id;
	protected final String unlocalizedName;
	protected final TextFormatting color;
	protected final Class<K> modifierType;
	protected final List<K> modifiers;
	protected T baseValue;
	protected boolean displayOnTooltip;

	/**
	 * @param id
	 * @param unlocalizedName
	 * @param type
	 * @param color
	 */
	public AbstractAttributeDefenition(ResourceLocation id, String unlocalizedName, TextFormatting color, Class<K> modifierType) {
		this.id = id;
		this.unlocalizedName = unlocalizedName;
		this.color = color;
		this.modifiers = new ArrayList<K>();
		this.modifierType = modifierType;
	}

	public abstract ITextComponent getTooltipValue();

	public Class<K> getModifierType() {
		return this.modifierType;
	}

	public ResourceLocation getId() {
		return id;
	}

	public boolean shouldDisplayOnTooltip() {
		return displayOnTooltip;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public TextFormatting getColor() {
		return color;
	}

	public void setBaseValue(T baseValue) {
		this.baseValue = baseValue;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT output = new CompoundNBT();
		
		// Serialize the id.
		output.putString("id", id.toString());
		
		// Serialize the tooltip enabled state.
		output.putBoolean("show_tooltip", displayOnTooltip);

		// Serialize the modifiers.
		ListNBT modifiersNbt = new ListNBT();
		modifiers.forEach(modifier -> {
			modifiersNbt.add(modifier.serialize());
		});
		output.put("modifiers", modifiersNbt);

		
		return output;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		displayOnTooltip = nbt.getBoolean("show_tooltip");
	}
}
