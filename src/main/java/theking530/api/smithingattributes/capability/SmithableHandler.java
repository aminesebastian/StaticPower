package theking530.api.smithingattributes.capability;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.smithingattributes.attributes.AbstractAttributeDefenition;
import theking530.api.smithingattributes.attributes.AttributeRegistry;
import theking530.api.smithingattributes.attributes.modifiers.AbstractAttributeModifier;
import theking530.staticcore.item.IItemMultiCapability;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;

public class SmithableHandler implements ISmithable, INBTSerializable<CompoundNBT>, IItemMultiCapability {
	private final HashMap<ResourceLocation, AbstractAttributeDefenition<?, ?>> attributes;
	private String name;
	private ItemStackMultiCapabilityProvider parent;

	public SmithableHandler(String name) {
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
	public <T, K extends AbstractAttributeModifier<T>> boolean addAttribute(AbstractAttributeDefenition<T, K> attribute) {
		if (hasAttribute(attribute.getId())) {
			return false;
		}
		attributes.put(attribute.getId(), attribute);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, K extends AbstractAttributeModifier<T>> AbstractAttributeDefenition<T, K> getAttribute(ResourceLocation attributeId) {
		return (AbstractAttributeDefenition<T, K>) attributes.get(attributeId);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT output = new CompoundNBT();
		CompoundNBT attributeList = new CompoundNBT();
		for (ResourceLocation id : attributes.keySet()) {
			CompoundNBT serializedAttribute = attributes.get(id).serializeNBT();
			attributeList.put(id.toString(), serializedAttribute);
		}
		output.put("attributes", attributeList);
		return null;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		CompoundNBT attributeList = nbt.getCompound("attributes");
		for (String id : attributeList.keySet()) {
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
		return new Capability[] { CapabilitySmithable.SMITHABLE_CAPABILITY };
	}

}
