package theking530.api.attributes.defenitions;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.attributes.modifiers.AbstractAttributeModifier;
import theking530.api.attributes.registration.AttributeModifierRegistry;

public abstract class AbstractAttributeDefenition<T, K extends AbstractAttributeModifier<?>> implements INBTSerializable<CompoundNBT> {
	protected final ResourceLocation id;
	protected final String unlocalizedName;
	protected final TextFormatting color;
	protected final Class<K> modifierType;
	protected final List<K> modifiers;
	protected T baseValue;

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
	
	public abstract T getValue();

	public abstract boolean shouldDisplayOnTooltip();

	public abstract boolean canAcceptModifier(K modifier);

	public abstract @Nullable IFormattableTextComponent getDifferenceLabel(AbstractAttributeDefenition<?, ?> other);

	protected abstract void serializeBaseValue(CompoundNBT nbt);

	protected abstract void deserializeBaseValue(CompoundNBT nbt);

	public IFormattableTextComponent getAttributeTitle(boolean showAdvanced) {
		return new TranslationTextComponent(getUnlocalizedName()).mergeStyle(getColor());
	}

	public void addAdditionalTooltipValues(List<ITextComponent> tooltip, boolean showAdvanced) {

	}

	public Class<K> getModifierType() {
		return this.modifierType;
	}

	@SuppressWarnings("unchecked")
	public T addModifierUnchecked(AbstractAttributeModifier<?> modifier, boolean simulate) {
		return addModifier((K) modifier, simulate);

	}

	public T addModifier(K modifier, boolean simulate) {
		// Add the modifier.
		modifiers.add(modifier);

		// Capture the output value.
		T output = getValue();

		// If we're simulating, undo the add.
		if (simulate) {
			modifiers.remove(modifier);
		}

		// Return the captured output.
		return output;
	}

	public ResourceLocation getId() {
		return id;
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

	public T getBaseValue() {
		return this.baseValue;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT output = new CompoundNBT();

		// Serialize the modifiers.
		ListNBT modifiersNbt = new ListNBT();
		modifiers.forEach(modifier -> {
			modifiersNbt.add(modifier.serialize());
		});
		output.put("modifiers", modifiersNbt);

		// Serialize the base value.
		serializeBaseValue(output);

		return output;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		// Deserialize the modifiers.
		modifiers.clear();
		ListNBT modifiersNbtList = nbt.getList("modifiers", Constants.NBT.TAG_COMPOUND);
		for (INBT modifierTag : modifiersNbtList) {
			// Get the modifier tag as a CompoundNBT.
			CompoundNBT modifierNbt = (CompoundNBT) modifierTag;

			// Create the modifier.
			AbstractAttributeModifier<?> modifier = AttributeModifierRegistry.createEmptyInstance(modifierNbt.getString("type"));

			// Deserialize the modifier.
			modifier.deserialize(modifierNbt);

			// Add the modifier.
			modifiers.add((K) modifier);
		}

		// Deserialize the base value.
		deserializeBaseValue(nbt);
	}
}
