package theking530.staticpower.cables.attachments.digistore.craftinginterface;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AttachmentTooltipUtilities;
import theking530.staticpower.cables.attachments.digistore.AbstractDigistoreCableAttachment;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class DigistoreCraftingInterfaceAttachment extends AbstractDigistoreCableAttachment {
	public static final String CRAFTING_INTERFACE_TIMER_TAG = "crafting_interface_timer";
	public static final String RECIPE_ITEM_TAG = "recipe_items";

	public DigistoreCraftingInterfaceAttachment(String name) {
		super(name);
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt)
				.addCapability(new ItemStackCapabilityInventory("default", stack, StaticPowerConfig.SERVER.digistoreCraftingInterfaceSlots.get()), (Direction) null)
				.addCapability(new ItemStackCapabilityInventory("upgrades", stack, 9));
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cableComponent) {
		super.onAddedToCable(attachment, side, cableComponent);
		attachment.getTag().putInt(CRAFTING_INTERFACE_TIMER_TAG, 0);
	}

	@Override
	public void onRemovedFromCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		super.onRemovedFromCable(attachment, side, cable);
		IItemHandler patternInv = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
		InventoryUtilities.clearInventory(patternInv);
	}

	@Override
	public void getAdditionalDrops(ItemStack attachment, AbstractCableProviderComponent cable, List<ItemStack> drops) {
		IItemHandler patternInv = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
		if (patternInv != null) {
			for (int i = 0; i < patternInv.getSlots(); i++) {
				ItemStack upgrade = patternInv.getStackInSlot(i);
				if (!upgrade.isEmpty()) {
					WorldUtilities.dropItem(cable.getWorld(), cable.getPos(), upgrade);
				}
			}
		}
	}

	@Override
	public long getPowerUsage(ItemStack attachment, DigistoreCableProviderComponent cableComponent) {
		return 1000;
	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getUIContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return new CraftingInterfaceContainerProvider(attachment, cable, attachmentSide);
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return true;
	}

	@Override
	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		if (cable.getWorld().isRemote) {
			return;
		}

		// Get the tile entity on the pulling side, return if it is null.
		TileEntity te = cable.getWorld().getTileEntity(cable.getPos().offset(side));
		if (te == null || te.isRemoved()) {
			return;
		}

		// Increment the extraction timer. If it returns true, attempt an item extract.
		if (increaseTimer(attachment)) {
			// See if we can perform a digistore extract and supply.
			supplyCraftingSupplies(attachment, side, cable, te);
		}
	}

	public boolean increaseTimer(ItemStack attachment) {
		if (!attachment.hasTag()) {
			attachment.setTag(new CompoundNBT());
		}
		if (!attachment.getTag().contains(CRAFTING_INTERFACE_TIMER_TAG)) {
			attachment.getTag().putInt(CRAFTING_INTERFACE_TIMER_TAG, 0);
		}

		// Get the current timer and the extraction rate.
		int currentTimer = attachment.getTag().getInt(CRAFTING_INTERFACE_TIMER_TAG);

		// Increment the current timer.
		currentTimer += 1;
		if (currentTimer >= StaticPowerConfig.SERVER.digistoreExporterRate.get()) {
			attachment.getTag().putInt(CRAFTING_INTERFACE_TIMER_TAG, 0);
			return true;
		} else {
			attachment.getTag().putInt(CRAFTING_INTERFACE_TIMER_TAG, currentTimer);
			return false;
		}
	}

	protected void supplyCraftingSupplies(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, TileEntity targetTe) {
		targetTe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).ifPresent(target -> {
			cable.<DigistoreNetworkModule>getNetworkModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE).ifPresent(module -> {
				// Return early if there is no manager.
				if (!module.isManagerPresent()) {
					return;
				}

				// Get the crafting item inventory (if there is a null value, do not handle it,
				// throw an exception).
				IItemHandlerModifiable processingItemInventory = (IItemHandlerModifiable) attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN)
						.orElseThrow(() -> new RuntimeException("Encounetered a supplier attachment without a valid filter inventory."));

				// If the processing inventory is empty, do nothing.
				if (InventoryUtilities.isInventoryEmpty(processingItemInventory)) {
					return;
				}

				// Attempt to supply all the items out.
				for (int i = 0; i < processingItemInventory.getSlots(); i++) {
					// Skip if the stack is empty.
					ItemStack stackInSlot = processingItemInventory.getStackInSlot(i);
					if (stackInSlot.isEmpty()) {
						continue;
					}

					// Attempt to insert the item. Store the remaining back into the processing
					// inventory.
					ItemStack remaining = InventoryUtilities.insertItemIntoInventory(target, stackInSlot, false);
					processingItemInventory.setStackInSlot(i, remaining);
				}
			});
		});
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		return StaticPowerAdditionalModels.CABLE_DIGISTORE_CRAFTING_INTERFACE_ATTACHMENT;
	}

	/**
	 * Gets all the patterns within this attachment.
	 * 
	 * @param attachment
	 * @return
	 */
	public static List<EncodedDigistorePattern> getAllPaternsInInterface(ItemStack attachment) {
		// Allocate the output.
		List<EncodedDigistorePattern> patterns = new LinkedList<EncodedDigistorePattern>();

		// Attempt to get the item filter inventory.
		IItemHandler patternInventory = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
		for (int i = 0; i < patternInventory.getSlots(); i++) {
			EncodedDigistorePattern pattern = EncodedDigistorePattern.readFromPatternCard(patternInventory.getStackInSlot(i));
			if (pattern != null) {
				patterns.add(pattern);
			}
		}
		return patterns;
	}

	/**
	 * Determines if this crafting interface is busy.
	 * 
	 * @param attachment
	 * @return
	 */
	public static boolean isCraftingInterfaceBusy(ItemStack attachment) {
		// If the recipe inventory is empty, we are good to process. Otherwise, we are
		// busy.
		IItemHandler processingItemInventory = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).orElse(null);
		return !InventoryUtilities.isInventoryEmpty(processingItemInventory);
	}

	/**
	 * Adds items to be distributed from this crafting interface. If all the items
	 * are added, returns true.
	 * 
	 * @param attachment
	 * @param items
	 * @return
	 */
	public static boolean addIngredientsToInterface(ItemStack attachment, List<ItemStack> items) {
		// If we are busy, return false.
		if (isCraftingInterfaceBusy(attachment)) {
			return false;
		}

		// Get the inventory. If it is null, return false.
		IItemHandler processingItemInventory = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).orElse(null);
		if (processingItemInventory == null) {
			return false;
		}

		// If it exists, insert all the items and return true.
		for (int i = 0; i < items.size(); i++) {
			InventoryUtilities.insertItemIntoInventory(processingItemInventory, items.get(i), false);
		}
		return true;
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		tooltip.add(new TranslationTextComponent("gui.staticpower.crafting_interface_tooltip"));
		AttachmentTooltipUtilities.addSlotsCountTooltip("gui.staticpower.slots", StaticPowerConfig.SERVER.digistoreCraftingInterfaceSlots.get(), tooltip);
	}

	protected class CraftingInterfaceContainerProvider extends AbstractCableAttachmentContainerProvider {
		public CraftingInterfaceContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
			return new ContainerDigistoreCraftingInterface(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
