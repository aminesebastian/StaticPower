package theking530.api.attributes.capability;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.rendering.ItemAttributeRegistration.AttributeRegistration;
import theking530.api.attributes.type.AttributeType;
import theking530.staticcore.utilities.NBTUtilities;

public class AttributeableHandler implements IAttributable, INBTSerializable<CompoundTag>, ICapabilityProvider {
	private final HashMap<AttributeType<?>, AttributeInstance<?>> attributes;

	public AttributeableHandler() {
		attributes = new HashMap<>();
	}

	@Override
	public Set<AttributeType<?>> getAllAttributes() {
		return attributes.keySet();
	}

	@Override
	public boolean hasAttribute(AttributeType<?> attribute) {
		return attributes.containsKey(attribute);
	}

	public <T> boolean addAttribute(AttributeRegistration<T> attribute) {
		return addAttribute(attribute.attribute(), attribute.baseValue());
	}

	@Override
	public <T> boolean addAttribute(AttributeType<T> attribute, T baseValue) {
		if (hasAttribute(attribute)) {
			return false;
		}
		attributes.put(attribute, attribute.createInstance(baseValue));
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> AttributeInstance<T> getAttribute(AttributeType<T> attributeId) {
		return (AttributeInstance<T>) attributes.get(attributeId);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();

		ListTag attributeTagList = NBTUtilities.serialize(attributes.values(), (attribute) -> attribute.serializeNBT());
		output.put("attributes", attributeTagList);

		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		List<AttributeInstance<?>> instances = NBTUtilities.deserialize(nbt.getList("attributes", Tag.TAG_COMPOUND), (tag) -> AttributeInstance.deserialize((CompoundTag) tag));
		for (AttributeInstance<?> instance : instances) {
			this.attributes.put(instance.getAttribute(), instance);
		}
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityAttributable.ATTRIBUTABLE_CAPABILITY) {
			return LazyOptional.of(() -> this).cast();
		}
		return LazyOptional.empty();
	}
}
