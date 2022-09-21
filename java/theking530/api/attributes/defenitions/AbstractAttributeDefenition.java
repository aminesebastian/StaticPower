package theking530.api.attributes.defenitions;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.AbstractAttributeModifier;
import theking530.api.attributes.registration.AttributeModifierRegistry;

public abstract class AbstractAttributeDefenition<T, K extends AbstractAttributeModifier<?>> implements INBTSerializable<CompoundTag> {
	protected final ResourceLocation id;
	protected final String unlocalizedName;
	protected final ChatFormatting color;
	protected final Class<K> modifierType;
	protected final List<K> modifiers;
	protected T baseValue;

	/**
	 * @param id
	 * @param unlocalizedName
	 * @param color
	 * @param modifierType
	 */
	public AbstractAttributeDefenition(ResourceLocation id, String unlocalizedName, ChatFormatting color, Class<K> modifierType) {
		this.id = id;
		this.unlocalizedName = unlocalizedName;
		this.color = color;
		this.modifiers = new ArrayList<K>();
		this.modifierType = modifierType;
	}

	public abstract T getValue();

	/**
	 * This method should return true when this attribute is active. When this
	 * returns false, the attribute will not appear on a tooltip, or rendered on an
	 * item, etc.
	 * 
	 * @return
	 */
	public abstract boolean isActive();

	public abstract boolean canAcceptModifier(IAttributable attributable, K modifier);

	public abstract @Nullable MutableComponent getDifferenceLabel(AbstractAttributeDefenition<?, ?> other);

	protected abstract void serializeBaseValue(CompoundTag nbt);

	protected abstract void deserializeBaseValue(CompoundTag nbt);

	public MutableComponent getAttributeTitle(boolean showAdvanced) {
		return new TranslatableComponent(getUnlocalizedName()).withStyle(getColor());
	}

	public void addAdditionalTooltipValues(List<Component> tooltip, boolean showAdvanced) {

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

	public ChatFormatting getColor() {
		return color;
	}

	public void setBaseValue(T baseValue) {
		this.baseValue = baseValue;
	}

	public T getBaseValue() {
		return this.baseValue;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();

		// Serialize the modifiers.
		ListTag modifiersNbt = new ListTag();
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
	public void deserializeNBT(CompoundTag nbt) {
		// Deserialize the modifiers.
		modifiers.clear();
		ListTag modifiersNbtList = nbt.getList("modifiers", Tag.TAG_COMPOUND);
		for (Tag modifierTag : modifiersNbtList) {
			// Get the modifier tag as a CompoundNBT.
			CompoundTag modifierNbt = (CompoundTag) modifierTag;

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
