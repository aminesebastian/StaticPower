package theking530.api.itemattributes.capability;

import java.util.Set;

import net.minecraft.util.ResourceLocation;
import theking530.api.itemattributes.attributes.AbstractAttributeDefenition;

public interface IAttributable {
	public Set<ResourceLocation> getAllAttributes();

	public boolean hasAttribute(ResourceLocation id);

	public boolean addAttribute(AbstractAttributeDefenition<?, ?> attribute);

	public AbstractAttributeDefenition<?, ?> getAttribute(ResourceLocation attributeId);
}