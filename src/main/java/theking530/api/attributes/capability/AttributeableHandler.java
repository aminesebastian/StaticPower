package theking530.api.attributes.capability;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.attributes.defenitions.AbstractAttributeDefenition;
import theking530.api.attributes.registration.AttributeRegistry;
import theking530.staticcore.item.IItemMultiCapability;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;

public class AttributeableHandler implements IAttributable, INBTSerializable<CompoundTag>, IItemMultiCapability {
	private final HashMap<ResourceLocation, AbstractAttributeDefenition<?, ?>> attributes;
	private String name;
	private ItemStackMultiCapabilityProvider parent;

	public AttributeableHandler(String name) {
		this.name = name;
		attributes = new HashMap<ResourceLocation, AbstractAttributeDefenition<?, ?>>();
	}

	@Override
	public Set<ResourceLocation> getAllAttributes() {
		return attributes.keySet();
	}

	@Override
	public boolean hasAttribute(ResourceLocation id) {
		return attributes.containsKey(id);
	}

	@Override
	public boolean addAttribute(ResourceLocation attributeId) {
		return addAttribute(AttributeRegistry.createInstance(attributeId));
	}

	@Override
	public boolean addAttribute(AbstractAttributeDefenition<?, ?> attribute) {
		if (hasAttribute(attribute.getId())) {
			return false;
		}
		attributes.put(attribute.getId(), attribute);
		return true;
	}

	@Override
	public AbstractAttributeDefenition<?, ?> getAttribute(ResourceLocation attributeId) {
		return attributes.get(attributeId);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		CompoundTag attributeList = new CompoundTag();
		for (ResourceLocation id : attributes.keySet()) {
			CompoundTag serializedAttribute = attributes.get(id).serializeNBT();
			attributeList.put(id.toString(), serializedAttribute);
		}
		output.put("attributes", attributeList);
		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		CompoundTag attributeList = nbt.getCompound("attributes");
		for (String id : attributeList.getAllKeys()) {
			AbstractAttributeDefenition<?, ?> attribute = AttributeRegistry.createInstance(new ResourceLocation(id));
			attribute.deserializeNBT(attributeList.getCompound(id));
			attributes.put(attribute.getId(), attribute);
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
