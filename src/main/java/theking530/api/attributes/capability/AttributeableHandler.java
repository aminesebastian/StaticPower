package theking530.api.attributes.capability;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.type.AttributeType;
import theking530.staticcore.item.IItemMultiCapability;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticpower.utilities.NBTUtilities;

public class AttributeableHandler implements IAttributable, INBTSerializable<CompoundTag>, IItemMultiCapability {
	private final HashMap<AttributeType<?>, AttributeInstance<?>> attributes;
	private String name;
	private ItemStackMultiCapabilityProvider parent;

	public AttributeableHandler(String name) {
		this.name = name;
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
	public String getName() {
		return name;
	}

	@Override
	public ItemStackMultiCapabilityProvider getOwningProvider() {
		return parent;
	}

	@Override
	public void setOwningProvider(ItemStackMultiCapabilityProvider owningProvider) {
		parent = owningProvider;
	}

	@Override
	public Capability<?>[] getCapabilityTypes() {
		return new Capability[] { CapabilityAttributable.ATTRIBUTABLE_CAPABILITY };
	}
}
