package theking530.api.attributes.type;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.registries.RegistryObject;
import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.AttributeModifierInstance;
import theking530.api.attributes.values.AttributeValueType;

public abstract class AttributeType<T> {
	protected final String unlocalizedName;
	protected final ChatFormatting color;
	protected final RegistryObject<AttributeValueType<T>> valueType;

	@SuppressWarnings("unchecked")
	public AttributeType(String unlocalizedName, ChatFormatting color, RegistryObject<? extends AttributeValueType<T>> valueType) {
		this.unlocalizedName = unlocalizedName;
		this.color = color;
		this.valueType = (RegistryObject<AttributeValueType<T>>) valueType;
	}

	/**
	 * This method should return true when this attribute is active. When this
	 * returns false, the attribute will not appear on a tooltip, or rendered on an
	 * item, etc.
	 *
	 * @return
	 */
	public abstract boolean isActive(AttributeInstance<T> instance);

	public abstract @Nullable MutableComponent getDifferenceLabel(AttributeInstance<T> first, AttributeInstance<T> second);

	public boolean canAcceptModifier(IAttributable attributable, AttributeInstance<T> instance, AttributeModifierInstance<T> modifier) {
		return true;
	}

	public MutableComponent getAttributeTitle(AttributeInstance<T> instance, boolean showAdvanced) {
		return Component.translatable(getUnlocalizedName()).withStyle(getColor());
	}

	public void addAdditionalTooltipValues(AttributeInstance<T> instance, List<Component> tooltip, boolean showAdvanced) {

	}

	public boolean canApplyToAttributable(IAttributable attributable) {
		return true;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public ChatFormatting getColor() {
		return color;
	}

	public AttributeValueType<T> getValueType() {
		return valueType.get();
	}

	public AttributeInstance<T> createInstance(T initialValue) {
		return new AttributeInstance<T>(this, initialValue);
	}
}
