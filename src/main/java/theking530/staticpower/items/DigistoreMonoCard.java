package theking530.staticpower.items;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.IDigistoreInventory;

public class DigistoreMonoCard extends DigistoreCard {

	public DigistoreMonoCard(String name, ResourceLocation tierType, ResourceLocation model) {
		this(name, tierType, model, false);
	}

	public DigistoreMonoCard(String name, ResourceLocation tierType, ResourceLocation model, boolean shouldGlow) {
		super(name, tierType, model, shouldGlow);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		// Get the item name.
		ITextComponent cardName = super.getDisplayName(stack);

		// If this card contains an item, append it's display name.
		IDigistoreInventory inventory = DigistoreCard.getInventory(stack);
		if (!inventory.getDigistoreStack(0).getStoredItem().isEmpty()) {
			cardName.appendText(" (").appendSibling(inventory.getDigistoreStack(0).getStoredItem().getDisplayName()).appendText(")");
		}

		// Return the final name.
		return cardName;
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		int capacity = StaticPowerDataRegistry.getTier(tierType).getDigistoreCapacity() * MAX_UNIQUE_ITEM_TYPES_PER_CARD;

		// Cover in case of integer overflow, we max at int.max.
		return new DigistoreInventoryCapabilityProvider(stack, 1, capacity < 0 ? Integer.MAX_VALUE : capacity, nbt);
	}
}
