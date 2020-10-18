package theking530.api.itemattributes.capability;

import java.util.Set;

import net.minecraft.util.ResourceLocation;
import theking530.api.itemattributes.attributes.AbstractAttributeDefenition;
import theking530.api.itemattributes.attributes.modifiers.AbstractAttributeModifier;

public interface IAttributable {
	public Set<ResourceLocation> getAllAttributes();

	public boolean hasAttribute(ResourceLocation id);

	public <T, K extends AbstractAttributeModifier<?>> boolean addAttribute(AbstractAttributeDefenition<T, K> attribute);

	public <T, K extends AbstractAttributeModifier<?>> AbstractAttributeDefenition<T, K> getAttribute(ResourceLocation attributeId);
}