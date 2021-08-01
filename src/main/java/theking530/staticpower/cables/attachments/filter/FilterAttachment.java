package theking530.staticpower.cables.attachments.filter;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.utilities.StaticPowerRarities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.attachments.AttachmentTooltipUtilities;
import theking530.staticpower.utilities.ItemUtilities;

public class FilterAttachment extends AbstractCableAttachment {
	private final ResourceLocation model;
	private final ResourceLocation tierType;

	public FilterAttachment(String name, ResourceLocation tierType, ResourceLocation model) {
		super(name);
		this.model = model;
		this.tierType = tierType;
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, StaticPowerConfig.getTier(tierType).cableFilterSlots.get()));
	}

	public boolean doesItemPassFilter(ItemStack attachment, ItemStack itemToTest, AbstractCableProviderComponent cableComponent) {
		// Get the filter inventory (if there is a null value, do not handle it, throw
		// an exception).
		IItemHandler filterItems = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				.orElseThrow(() -> new RuntimeException("Encounetered an filter attachment without a valid filter inventory."));

		// Get the list of filter items.
		List<ItemStack> filterItemList = new LinkedList<ItemStack>();
		for (int i = 0; i < filterItems.getSlots(); i++) {
			if (!filterItems.getStackInSlot(i).isEmpty()) {
				filterItemList.add(filterItems.getStackInSlot(i).copy());
			}
		}

		// If there are no items in the filter list, then this attachment is NOT
		// filtering. Allow any items to be extracted.
		if (filterItemList.size() == 0) {
			return true;
		}

		// Check to see if the provided item matches.
		return ItemUtilities.filterItems(filterItemList, itemToTest, true, false, false, false);
	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getUIContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return new FilterContainerProvider(attachment, cable, attachmentSide);
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return true;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		return model;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return StaticPowerRarities.getRarityForTier(this.tierType);
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		tooltip.add(new TranslationTextComponent("gui.staticpower.filter_tooltip"));
		AttachmentTooltipUtilities.addSlotsCountTooltip("gui.staticpower.slots", StaticPowerConfig.getTier(tierType).cableFilterSlots.get(), tooltip);
	}

	protected class FilterContainerProvider extends AbstractCableAttachmentContainerProvider {
		public FilterContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
			return new ContainerFilter(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
