package theking530.staticpower.cables.attachments.retirever;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.utilities.StaticPowerRarities;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.attachments.AttachmentTooltipUtilities;
import theking530.staticpower.cables.item.ItemNetworkModule;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.utilities.InventoryUtilities;

public class RetrieverAttachment extends AbstractCableAttachment {
	public static final String RETRIEVEAL_TIMER_TAG = "retrieval_timer";
	private final ResourceLocation model;
	private final ResourceLocation tierType;

	public RetrieverAttachment(String name, ResourceLocation tierType, ResourceLocation model) {
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
		return new ItemStackMultiCapabilityProvider(stack, nbt)
				.addCapability(new ItemStackCapabilityInventory("default", stack, TierReloadListener.getTier(tierType).getCableRetrievalFilterSlots()));
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		super.onAddedToCable(attachment, side, cable);
		attachment.getTag().putInt(RETRIEVEAL_TIMER_TAG, 0);
	}

	@Override
	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		if (cable.getWorld().isRemote || !cable.doesAttachmentPassRedstoneTest(attachment)) {
			return;
		}

		// Increment the retrieval timer. If it returns true, continue. If not, stop.
		if (!incrementRetrievalTimer(attachment)) {
			return;
		}

		// Get the tile entity on the inserting side, return if it is null.
		TileEntity targetInventory = cable.getWorld().getTileEntity(cable.getPos().offset(side));
		if (targetInventory == null || targetInventory.isRemoved()) {
			return;
		}

		// If the filter inventory is false, do nothing.
		IItemHandler filterInventory = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);

		// Attempt to retrieve the item.
		cable.<ItemNetworkModule>getNetworkModule(CableNetworkModuleTypes.ITEM_NETWORK_MODULE).ifPresent(network -> {
			for (int i = 0; i < filterInventory.getSlots(); i++) {
				ItemStack filterItem = InventoryUtilities.getRandomItemStackFromInventory(filterInventory, 1, true);
				if (filterItem.isEmpty()) {
					continue;
				}

				// If we're able to retrieve an item, break.
				if (network.retrieveItemStack(filterItem, TierReloadListener.getTier(tierType).getCableRetrievalStackSize(), cable.getPos().offset(side),
						TierReloadListener.getTier(tierType).getRetrievedItemInitialSpeed())) {
					break;
				}
			}
		});
	}

	public boolean incrementRetrievalTimer(ItemStack attachment) {
		if (!attachment.hasTag()) {
			attachment.setTag(new CompoundNBT());
		}
		if (!attachment.getTag().contains(RETRIEVEAL_TIMER_TAG)) {
			attachment.getTag().putInt(RETRIEVEAL_TIMER_TAG, 0);
		}

		// Get the current timer and the extraction rate.
		int currentTimer = attachment.getTag().getInt(RETRIEVEAL_TIMER_TAG);

		// Increment the current timer.
		currentTimer += 1;
		if (currentTimer >= TierReloadListener.getTier(tierType).getCableRetrievalRate()) {
			attachment.getTag().putInt(RETRIEVEAL_TIMER_TAG, 0);
			return true;
		} else {
			attachment.getTag().putInt(RETRIEVEAL_TIMER_TAG, currentTimer);

			return false;
		}
	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
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
		AttachmentTooltipUtilities.addSlotsCountTooltip("gui.staticpower.slots", TierReloadListener.getTier(tierType).getCableRetrievalFilterSlots(), tooltip);
	}

	protected class FilterContainerProvider extends AbstractCableAttachmentContainerProvider {
		public FilterContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
			return new ContainerRetriever(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
