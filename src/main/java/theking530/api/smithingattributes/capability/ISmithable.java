package theking530.api.smithingattributes.capability;

import java.util.Set;

import net.minecraft.util.ResourceLocation;
import theking530.api.smithingattributes.attributes.AbstractAttributeDefenition;
import theking530.api.smithingattributes.attributes.modifiers.AbstractAttributeModifier;

public interface ISmithable {
	public Set<ResourceLocation> getAllAttributes();

	public boolean hasAttribute(ResourceLocation id);

	public <T, K extends AbstractAttributeModifier<T>> boolean addAttribute(AbstractAttributeDefenition<T, K> attribute);

	public <T, K extends AbstractAttributeModifier<T>> AbstractAttributeDefenition<T, K> getAttribute(ResourceLocation attributeId);
}