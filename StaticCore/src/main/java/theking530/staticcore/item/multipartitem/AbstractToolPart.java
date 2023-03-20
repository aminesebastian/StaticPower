package theking530.staticcore.item.multipartitem;

import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.type.AttributeType;
import theking530.staticcore.attribiutes.types.AbstractHardenedAttributeType;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticcore.item.StaticCoreItem;

public abstract class AbstractToolPart extends StaticCoreItem implements ICustomModelProvider {
	protected final ResourceLocation tier;

	public AbstractToolPart(ResourceLocation tier, Properties properties) {
		super(properties);
		this.tier = tier;
	}

	public ResourceLocation getTier() {
		return this.tier;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getMaxDamage(ItemStack stack) {
		// Get the base durability.
		AtomicInteger baseDurability = new AtomicInteger(getBaseDurability());

		// Apply hardening if possible.
		stack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).ifPresent(attributable -> {

			// Check to see if we have an activated hardening attribute.
			for (AttributeType<?> attribute : attributable.getAllAttributes()) {
				// Get the instance.
				AttributeInstance<?> attributeInstance = attributable.getAttribute(attribute);

				// If it is an abstract hardened defenition and it IS activated.
				if (attribute instanceof AbstractHardenedAttributeType && attributeInstance.isActive()) {
					// Get an instance.
					AbstractHardenedAttributeType attrib = (AbstractHardenedAttributeType) attribute;

					// Apply the durability change. Then break the loop.
					baseDurability.set(attrib.applyHardening((AttributeInstance<Boolean>) attributeInstance, baseDurability.get()));
					break;
				}
			}
		});

		// Return the durability.
		return baseDurability.get();
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

//	@Override
//	public boolean isFoil(ItemStack stack) {
//		return this.tier == StaticCoreTiers.CREATIVE;
//	}

	protected abstract int getBaseDurability();
}
