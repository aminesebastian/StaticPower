package theking530.staticpower.cables.attachments.filter;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.blockentity.components.AbstractCableProviderComponent;
import theking530.staticcore.cablenetwork.attachment.AbstractCableAttachment;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.utilities.StaticPowerRarities;
import theking530.staticcore.utilities.item.ItemUtilities;
import theking530.staticpower.cables.attachments.AttachmentTooltipUtilities;
import theking530.staticpower.init.ModCreativeTabs;

public class FilterAttachment extends AbstractCableAttachment {
	private final ResourceLocation model;
	private final ResourceLocation tierType;

	public FilterAttachment(ResourceLocation tierType, ResourceLocation model) {
		super(ModCreativeTabs.CABLES);
		this.model = model;
		this.tierType = tierType;
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		int slots = !StaticCoreConfig.isConfigLoaded(tierType) ? 0 : StaticCoreConfig.getTier(tierType).cableAttachmentConfiguration.cableFilterSlots.get();
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, slots));
	}

	public boolean doesItemPassFilter(ItemStack attachment, ItemStack itemToTest, AbstractCableProviderComponent cableComponent) {
		// Get the filter inventory (if there is a null value, do not handle it, throw
		// an exception).
		IItemHandler filterItems = attachment.getCapability(ForgeCapabilities.ITEM_HANDLER)
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
	public ResourceLocation getModel(ItemStack attachment, BlockAndTintGetter level, BlockPos pos) {
		return model;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return StaticPowerRarities.getRarityForTier(this.tierType);
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(Component.translatable("gui.staticpower.filter_tooltip"));
		AttachmentTooltipUtilities.addSlotsCountTooltip("gui.staticpower.slots", StaticCoreConfig.getTier(tierType).cableAttachmentConfiguration.cableFilterSlots.get(), tooltip);
	}

	protected class FilterContainerProvider extends AbstractCableAttachmentContainerProvider {
		public FilterContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
			return new ContainerFilter(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
