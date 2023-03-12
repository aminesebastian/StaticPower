package theking530.api.attributes.rendering;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import theking530.api.attributes.type.AttributeType;

public class ItemAttributeType {
	private final Supplier<? extends AttributeType<?>> attribute;
	private final Supplier<? extends Item> item;
	private final AbstractAttributeRenderLayer renderLayer;

	public ItemAttributeType(Supplier<? extends AttributeType<?>> attribute, Supplier<? extends Item> item, AbstractAttributeRenderLayer renderLayer) {
		this.attribute = attribute;
		this.renderLayer = renderLayer;
		this.item = item;
	}

	public AttributeType<?> getAttribute() {
		return attribute.get();
	}

	public AbstractAttributeRenderLayer getRenderLayer() {
		return renderLayer;
	}

	public Item getItem() {
		return item.get();
	}
}
