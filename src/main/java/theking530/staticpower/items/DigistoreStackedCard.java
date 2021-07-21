package theking530.staticpower.items;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.api.digistore.DigistoreInventoryCapabilityProvider;
import theking530.staticpower.StaticPowerConfig;

public class DigistoreStackedCard extends DigistoreCard {

	public DigistoreStackedCard(String name, ResourceLocation tierType, ResourceLocation model) {
		this(name, tierType, model, false);
	}

	public DigistoreStackedCard(String name, ResourceLocation tierType, ResourceLocation model, boolean shouldGlow) {
		super(name, tierType, model, shouldGlow);
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		float capacityMultiplier = 64.0f / StaticPowerConfig.SERVER.digistoreStackedCardUniqueTypes.get();
		return new DigistoreInventoryCapabilityProvider(stack, StaticPowerConfig.SERVER.digistoreStackedCardUniqueTypes.get(),
				(int) (StaticPowerConfig.getTier(tierType).digistoreCardCapacity.get() * capacityMultiplier), nbt);
	}
}
