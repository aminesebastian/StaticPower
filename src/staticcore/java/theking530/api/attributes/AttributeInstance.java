package theking530.api.attributes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.AttributeModifierInstance;
import theking530.api.attributes.type.AttributeType;
import theking530.staticcore.StaticCoreRegistries;

public class AttributeInstance<T> implements INBTSerializable<CompoundTag> {
	protected final AttributeType<T> attribute;
	protected final List<AttributeModifierInstance<T>> modifiers;
	protected T baseValue;

	public AttributeInstance(AttributeType<T> type, T initialValue) {
		this(type);
		baseValue = initialValue;
	}

	public AttributeInstance(AttributeType<T> type) {
		this.attribute = type;
		this.modifiers = new ArrayList<AttributeModifierInstance<T>>();
	}

	/**
	 * This method should return true when this attribute is active. When this
	 * returns false, the attribute will not appear on a tooltip, or rendered on an
	 * item, etc.
	 *
	 * @return
	 */
	public boolean isActive() {
		return attribute.isActive(this);
	}

	public List<AttributeModifierInstance<T>> getModifiers() {
		return modifiers;
	}

	public boolean canAcceptModifier(IAttributable attributable, AttributeModifierInstance<T> modifier) {
		return attribute.canAcceptModifier(attributable, this, modifier);
	}

	public @Nullable MutableComponent getDifferenceLabel(AttributeInstance<T> other) {
		return attribute.getDifferenceLabel(this, other);
	}

	public AttributeType<T> getAttribute() {
		return attribute;
	}

	public MutableComponent getAttributeTitle(boolean showAdvanced) {
		return attribute.getAttributeTitle(this, showAdvanced);
	}

	public void addAdditionalTooltipValues(List<Component> tooltip, boolean showAdvanced) {
		attribute.addAdditionalTooltipValues(this, tooltip, showAdvanced);
	}

	@SuppressWarnings("unchecked")
	public T addModifierUnchecked(AttributeModifierInstance<?> modifier, boolean simulate) {
		return addModifier((AttributeModifierInstance<T>) modifier, simulate);

	}

	public T addModifier(AttributeModifierInstance<T> modifier, boolean simulate) {
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

	public T getValue() {
		T output = getBaseValue();
		for (AttributeModifierInstance<T> modifier : modifiers) {
			output = modifier.modifyValue(output);
		}
		return output;
	}

	public T getBaseValue() {
		return this.baseValue;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putString("type", StaticCoreRegistries.Attribute().getKey(attribute).toString());
		attribute.getValueType().serializeValue(baseValue, output);

		// Serialize the modifiers.
		ListTag modifiersNbt = new ListTag();
		modifiers.forEach(modifier -> {
			CompoundTag tag = new CompoundTag();
			modifier.serialize(tag);
			modifiersNbt.add(tag);
		});
		output.put("modifiers", modifiersNbt);

		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		// Deserialize the modifiers.
		modifiers.clear();
		ListTag modifiersNbtList = nbt.getList("modifiers", Tag.TAG_COMPOUND);
		for (Tag modifierTag : modifiersNbtList) {
			// Get the modifier tag as a CompoundNBT.
			CompoundTag modifierNbt = (CompoundTag) modifierTag;
			AttributeModifierInstance<T> instance = AttributeModifierInstance.deserialize(modifierNbt);
			modifiers.add(instance);
		}

		baseValue = attribute.getValueType().deserializeValue(nbt);
	}

	@SuppressWarnings("unchecked")
	public static <T> AttributeInstance<T> deserialize(CompoundTag nbt) {
		ResourceLocation attributeType = new ResourceLocation(nbt.getString("type"));
		AttributeType<T> attribute = (AttributeType<T>) StaticCoreRegistries.Attribute().getValue(attributeType);

		AttributeInstance<T> instance = new AttributeInstance<T>(attribute);
		instance.deserializeNBT(nbt);
		return instance;
	}
}