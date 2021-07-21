package theking530.staticpower.items;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.api.digistore.DigistoreInventoryCapabilityProvider;
import theking530.api.digistore.IDigistoreInventory;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.items.DigistoreMonoCardItemModel;

public class DigistoreMonoCard extends DigistoreCard implements ICustomModelSupplier {

	public DigistoreMonoCard(String name, ResourceLocation tierType, ResourceLocation model) {
		this(name, tierType, model, false);
	}

	public DigistoreMonoCard(String name, ResourceLocation tierType, ResourceLocation model, boolean shouldGlow) {
		super(name, tierType, model, shouldGlow);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		// Get the item name.
		IFormattableTextComponent cardName = (IFormattableTextComponent) super.getDisplayName(stack);

		// If this card contains an item, append it's display name.
		IDigistoreInventory inventory = DigistoreCard.getInventory(stack);
		if (!inventory.getDigistoreStack(0).getStoredItem().isEmpty()) {
			cardName.appendString(" (").append(inventory.getDigistoreStack(0).getStoredItem().getDisplayName()).appendString(")");
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
		int capacity = StaticPowerConfig.getTier(tierType).digistoreCardCapacity.get() * (StaticPowerConfig.SERVER.digistoreCardUniqueTypes.get() / 8);

		// Cover in case of integer overflow, we max at int.max.
		return new DigistoreInventoryCapabilityProvider(stack, 1, capacity < 0 ? Integer.MAX_VALUE : capacity, nbt);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new DigistoreMonoCardItemModel(existingModel);
	}
}
