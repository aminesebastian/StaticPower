package theking530.staticpower.cables.attachments.extractor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.StaticPowerRarities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.attachments.AttachmentTooltipUtilities;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.fluid.FluidCableComponent;
import theking530.staticpower.cables.fluid.FluidNetworkModule;
import theking530.staticpower.cables.item.ItemNetworkModule;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerAttachmentDataContainer;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.digistorenetwork.ioport.TileEntityDigistoreIOPort;
import theking530.staticpower.utilities.ItemUtilities;

public class ExtractorAttachment extends AbstractCableAttachment {
	/**
	 * When this property is added to a cable and its true, nothing can be inserted
	 * through this attachment to the attached tile.
	 */
	public static final String INPUT_BLOCKED = "input_blocked";
	public static final String EXTRACTION_TIMER_TAG = "extraction_timer";
	private final ResourceLocation tierType;
	private final ResourceLocation model;

	public ExtractorAttachment(String name, ResourceLocation tierType, ResourceLocation model) {
		super(name);
		this.tierType = tierType;
		this.model = model;
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt)
				.addCapability(new ItemStackCapabilityInventory("default", stack, StaticPowerConfig.getTier(tierType).cableExtractionFilterSlots.get()));
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cableComponent) {
		super.onAddedToCable(attachment, side, cableComponent);
		attachment.getTag().putInt(EXTRACTION_TIMER_TAG, 0);
	}

	@Override
	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		if (cable.getWorld().isRemote || !cable.doesAttachmentPassRedstoneTest(attachment)) {
			return;
		}

		// Get the tile entity on the pulling side, return if it is null.
		TileEntity te = cable.getWorld().getTileEntity(cable.getPos().offset(side));
		if (te == null || te.isRemoved()) {
			return;
		}

		// Increment the extraction timer. If it returns true, attempt an item extract.
		if (incrementExtractionTimer(attachment)) {
			// See if we can perform a digistore extract.
			if (!performDigistoreExtract(attachment, side, cable, te)) {
				// If not, attempt to transfer the item from a regular inventory.
				performItemHandlerExtract(attachment, side, cable, te);
			}
		}

		// Attempt to transfer fluid regardless of the item extract timer.
		performFluidExtract(attachment, side, cable, te);
	}

	public boolean doesItemPassExtractionFilter(ItemStack attachment, ItemStack itemToTest) {
		// Get the filter inventory (if there is a null value, do not handle it, throw
		// an exception).
		IItemHandler filterItems = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				.orElseThrow(() -> new RuntimeException("Encounetered an extractor attachment without a valid filter inventory."));

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

	public boolean incrementExtractionTimer(ItemStack attachment) {

		if (!attachment.hasTag()) {
			attachment.setTag(new CompoundNBT());
		}
		if (!attachment.getTag().contains(EXTRACTION_TIMER_TAG)) {
			attachment.getTag().putInt(EXTRACTION_TIMER_TAG, 0);
		}

		// Get the current timer and the extraction rate.
		int currentTimer = attachment.getTag().getInt(EXTRACTION_TIMER_TAG);

		// Increment the current timer.
		currentTimer += 1;
		if (currentTimer >= StaticPowerConfig.getTier(tierType).cableExtractorRate.get()) {
			attachment.getTag().putInt(EXTRACTION_TIMER_TAG, 0);
			return true;
		} else {
			attachment.getTag().putInt(EXTRACTION_TIMER_TAG, currentTimer);

			return false;
		}

	}

	@Override
	public void initializeServerDataContainer(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, ServerAttachmentDataContainer dataContainer) {
		dataContainer.addProperty(INPUT_BLOCKED, true);
	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getUIContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return new ExtractorContainerProvider(attachment, cable, attachmentSide);
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

	protected boolean performDigistoreExtract(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, TileEntity targetTe) {
		if (targetTe instanceof TileEntityDigistoreIOPort) {
			AtomicBoolean output = new AtomicBoolean(false);
			((TileEntityDigistoreIOPort) targetTe).digistoreCableProvider.<DigistoreNetworkModule>getNetworkModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE).ifPresent(module -> {
				cable.<ItemNetworkModule>getNetworkModule(CableNetworkModuleTypes.ITEM_NETWORK_MODULE).ifPresent(network -> {
					// Return early if there is no manager.
					if (!module.isManagerPresent()) {
						return;
					}

					// Get the filter inventory (if there is a null value, do not handle it, throw
					// an exception).
					IItemHandler filterItems = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
							.orElseThrow(() -> new RuntimeException("Encounetered an extractor attachment without a valid filter inventory."));

					// We do this to ensure we randomly extract.
					int startingSlot = SDMath.getRandomIntInRange(0, filterItems.getSlots() - 1);
					int currentSlot = startingSlot;

					// Get the list of filter items.
					for (int i = 0; i < filterItems.getSlots(); i++) {
						if (!filterItems.getStackInSlot(currentSlot).isEmpty()) {
							// Simulate an extract.
							ItemStack extractedItem = module.extractItem(filterItems.getStackInSlot(currentSlot), StaticPowerConfig.getTier(tierType).cableExtractionStackSize.get(), true);

							// Increment the current slot and make sure we wrap around.
							currentSlot++;
							if (currentSlot > filterItems.getSlots() - 1) {
								currentSlot = 0;
							}

							// If the extracted item is empty, continue.
							if (extractedItem.isEmpty()) {
								continue;
							}

							// Attempt to transfer the itemstack through the cable network.
							ItemStack remainingAmount = network.transferItemStack(extractedItem, cable.getPos(), side.getOpposite(), false,
									StaticPowerConfig.getTier(tierType).cableExtractedItemInitialSpeed.get());
							if (remainingAmount.getCount() < extractedItem.getCount()) {
								module.extractItem(extractedItem, extractedItem.getCount() - remainingAmount.getCount(), false);
								cable.getTileEntity().markDirty();
								output.set(true);
								break;
							}
						}
					}
				});
			});
			return output.get();
		}
		return false;
	}

	protected void performItemHandlerExtract(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, TileEntity targetTe) {
		cable.<ItemNetworkModule>getNetworkModule(CableNetworkModuleTypes.ITEM_NETWORK_MODULE).ifPresent(network -> {
			// Attempt to extract an item.
			targetTe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).ifPresent(inv -> {
				for (int i = 0; i < inv.getSlots(); i++) {
					// Simulate an extract.
					ItemStack extractedItem = inv.extractItem(i, StaticPowerConfig.getTier(tierType).cableExtractionStackSize.get(), true);

					// If the extracted item is empty, continue.
					if (extractedItem.isEmpty()) {
						continue;
					}

					// Skip any items that are not supported by the extraction filter.
					if (!doesItemPassExtractionFilter(attachment, extractedItem)) {
						continue;
					}

					// Attempt to transfer the itemstack through the cable network.
					ItemStack remainingAmount = network.transferItemStack(extractedItem, cable.getPos(), side.getOpposite(), false,
							StaticPowerConfig.getTier(tierType).cableExtractedItemInitialSpeed.get());
					if (remainingAmount.getCount() < extractedItem.getCount()) {
						inv.extractItem(i, extractedItem.getCount() - remainingAmount.getCount(), false);
						cable.getTileEntity().markDirty();
						break;
					}
				}
			});
		});
	}

	protected void performFluidExtract(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, TileEntity targetTe) {
		cable.<FluidNetworkModule>getNetworkModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE).ifPresent(network -> {
			FluidCableComponent fluidCable = (FluidCableComponent) cable;
			// Attempt to extract an item.
			targetTe.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()).ifPresent(tank -> {
				if (tank.getTanks() <= 0) {
					return;
				}
				if (fluidCable.isFluidValid(0, tank.getFluidInTank(0))) {
					FluidStack drained = tank.drain(StaticPowerConfig.getTier(tierType).cableExtractionFluidRate.get(), FluidAction.SIMULATE);
					int filled = fluidCable.fill(drained, FluidAction.EXECUTE);
					tank.drain(filled, FluidAction.EXECUTE);
				}
			});
		});
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		AttachmentTooltipUtilities.addSlotsCountTooltip("gui.staticpower.slots", StaticPowerConfig.getTier(tierType).cableExtractionFilterSlots.get(), tooltip);
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent(""));
		tooltip.add(new TranslationTextComponent("gui.staticpower.extractor_rate_format", TextFormatting.AQUA.toString() + StaticPowerConfig.getTier(tierType).cableExtractorRate.get()));
		tooltip.add(new StringTextComponent("• ").append(
				new TranslationTextComponent("gui.staticpower.extractor_stack_size", TextFormatting.GOLD.toString() + StaticPowerConfig.getTier(tierType).cableExtractionStackSize.get())));
		tooltip.add(new StringTextComponent("• ").append(new TranslationTextComponent("gui.staticpower.extractor_fluid_rate",
				TextFormatting.BLUE + GuiTextUtilities.formatFluidToString(StaticPowerConfig.getTier(tierType).cableExtractionFluidRate.get()).getString())));

		double blocksPerTick = StaticPowerConfig.getTier(tierType).cableExtractedItemInitialSpeed.get();
		tooltip.add(new StringTextComponent("• ").append(new TranslationTextComponent("gui.staticpower.cable_transfer_rate",
				TextFormatting.GREEN + GuiTextUtilities.formatUnitRateToString(blocksPerTick).getString(), new TranslationTextComponent("gui.staticpower.blocks").getString())));
	}

	protected class ExtractorContainerProvider extends AbstractCableAttachmentContainerProvider {
		public ExtractorContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
			return new ContainerExtractor(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
