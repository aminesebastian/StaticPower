package theking530.staticpower.items.cableattachments.retirever;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.items.ItemStackInventoryCapabilityProvider;
import theking530.staticpower.items.cableattachments.AbstractCableAttachment;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.cables.network.modules.ItemNetworkModule;
import theking530.staticpower.tileentities.cables.network.modules.factories.CableNetworkModuleTypes;
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
		return new ItemStackInventoryCapabilityProvider(stack, StaticPowerDataRegistry.getTier(tierType).getCableRetrievalFilterSize(), nbt);
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
				ItemStack filterItem = InventoryUtilities.getRandomItemStackFromInventory(filterInventory);
				if (filterItem.isEmpty()) {
					continue;
				}

				// If we're able to retrieve an item, break.
				if (network.retrieveItemStack(filterItem, StaticPowerDataRegistry.getTier(tierType).getCableRetrievalStackSize(), cable.getPos().offset(side))) {
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
		if (currentTimer >= StaticPowerDataRegistry.getTier(tierType).getCableRetrievalRate()) {
			attachment.getTag().putInt(RETRIEVEAL_TIMER_TAG, 0);
			return true;
		} else {
			attachment.getTag().putInt(RETRIEVEAL_TIMER_TAG, currentTimer);

			return false;
		}
	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getContainerProvider(ItemStack attachment) {
		return new FilterContainerProvider(attachment);
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return true;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		return model;
	}

	protected class FilterContainerProvider extends AbstractCableAttachmentContainerProvider {
		private Direction attachmentSide;
		private AbstractCableProviderComponent cableComponent;

		public FilterContainerProvider(ItemStack stack) {
			super(stack);
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
			return new ContainerRetriever(windowId, playerInv, targetItemStack, attachmentSide, cableComponent);
		}
	}
}
