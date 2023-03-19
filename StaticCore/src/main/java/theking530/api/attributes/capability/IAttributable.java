package theking530.api.attributes.capability;

import java.util.Set;

import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.type.AttributeType;

public interface IAttributable {
	public Set<AttributeType<?>> getAllAttributes();

	public boolean hasAttribute(AttributeType<?> id);

	public <T> boolean addAttribute(AttributeType<T> attribute, T baseValue);

	public <T> AttributeInstance<T> getAttribute(AttributeType<T> attribute);

	public default <T> T getAttributeValue(AttributeType<T> attribute) {
		return getAttribute(attribute).getValue();
	}
}