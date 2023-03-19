package theking530.staticpower.items;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.api.digistore.DigistoreInventoryCapabilityProvider;
import theking530.staticpower.StaticPowerConfig;

public class DigistoreStackedCard extends DigistoreCard {

	public DigistoreStackedCard(ResourceLocation tierType, ResourceLocation model) {
		this(tierType, model, false);
	}

	public DigistoreStackedCard(ResourceLocation tierType, ResourceLocation model, boolean shouldGlow) {
		super(tierType, model, shouldGlow);
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		if (StaticPowerConfig.SERVER_SPEC.isLoaded()) {
			float capacityMultiplier = 64.0f / StaticPowerConfig.SERVER.digistoreStackedCardUniqueTypes.get();
			return new DigistoreInventoryCapabilityProvider(stack, StaticPowerConfig.SERVER.digistoreStackedCardUniqueTypes.get(),
					(int) (StaticPowerConfig.getTier(tierType).digistoreCardCapacity.get() * capacityMultiplier), nbt);
		}
		return null;
	}
}
