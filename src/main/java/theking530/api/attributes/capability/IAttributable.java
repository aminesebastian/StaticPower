package theking530.api.attributes.capability;

import java.util.Set;

import net.minecraft.util.ResourceLocation;
import theking530.api.attributes.defenitions.AbstractAttributeDefenition;

public interface IAttributable {
	public Set<ResourceLocation> getAllAttributes();

	public boolean hasAttribute(ResourceLocation id);

	public boolean addAttribute(ResourceLocation attributeId);

	public boolean addAttribute(AbstractAttributeDefenition<?, ?> attribute);

	public AbstractAttributeDefenition<?, ?> getAttribute(ResourceLocation attributeId);
}