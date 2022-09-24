package theking530.api.multipartitem;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.api.attributes.capability.AttributeableHandler;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.defenitions.AbstractAttributeDefenition;
import theking530.api.attributes.defenitions.AbstractHardenedDefenition;
import theking530.api.attributes.rendering.AttributableItemRenderLayers;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.StaticPowerItem;

public abstract class AbstractToolPart extends StaticPowerItem implements ICustomModelSupplier {
	protected final ResourceLocation tier;
	protected final AttributableItemRenderLayers renderLayers;

	public AbstractToolPart(ResourceLocation tier, Properties properties) {
		super(properties);
		this.tier = tier;
		this.renderLayers = new AttributableItemRenderLayers();
		initializeRenderLayers(renderLayers); // Do this in the constructor as we only want this one ONCE.
	}

	public ResourceLocation getTier() {
		return this.tier;
	}

	public AttributableItemRenderLayers getRenderLayers() {
		return this.renderLayers;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		AttributeableHandler handler = new AttributeableHandler("attributes");
		initializeAttributes(handler);
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(handler);
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		// Get the base durability.
		AtomicInteger baseDurability = new AtomicInteger(getBaseDurability());

		// Apply hardening if possible.
		stack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).ifPresent(attributable -> {

			// Check to see if we have an activated hardening attribute.
			for (ResourceLocation id : attributable.getAllAttributes()) {
				// Get the instance.
				AbstractAttributeDefenition<?, ?> attributeInstance = attributable.getAttribute(id);

				// If it is an abstract hardened defenition and it IS activated.
				if (attributeInstance instanceof AbstractHardenedDefenition && ((AbstractHardenedDefenition) attributeInstance).getValue()) {
					// Get an instance.
					AbstractHardenedDefenition attrib = (AbstractHardenedDefenition) attributable.getAttribute(id);

					// Apply the durability change. Then break the loop.
					baseDurability.set(attrib.applyHardening(baseDurability.get()));
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

	@Override
	public boolean isFoil(ItemStack stack) {
		return this.tier == StaticPowerTiers.CREATIVE;
	}

	protected abstract int getBaseDurability();

	protected abstract void initializeAttributes(AttributeableHandler handler);

	protected abstract void initializeRenderLayers(AttributableItemRenderLayers renderLayers);
}
