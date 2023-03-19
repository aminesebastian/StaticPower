package theking530.staticpower.cables.attachments.extractor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.cablenetwork.AbstractCableProviderComponent;
import theking530.staticcore.cablenetwork.attachment.AbstractCableAttachment;
import theking530.staticcore.gui.GuiTextUtilities;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.utilities.StaticPowerRarities;
import theking530.staticcore.utilities.item.ItemUtilities;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.digistorenetwork.ioport.BlockEntityDigistoreIOPort;
import theking530.staticpower.cables.attachments.AttachmentTooltipUtilities;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.fluid.FluidCableComponent;
import theking530.staticpower.cables.fluid.FluidNetworkModule;
import theking530.staticpower.cables.item.ItemNetworkModule;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.cables.ModCableModules;

public class ExtractorAttachment extends AbstractCableAttachment {
	/**
	 * When this property is added to a cable and its true, nothing can be inserted
	 * through this attachment to the attached tile.
	 */
	public static final String INPUT_BLOCKED = "input_blocked";
	public static final String EXTRACTION_TIMER_TAG = "extraction_timer";
	private final ResourceLocation tierType;
	private final ResourceLocation model;

	public ExtractorAttachment(ResourceLocation tierType, ResourceLocation model) {
		super(ModCreativeTabs.CABLES);
		this.tierType = tierType;
		this.model = model;
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		int slots = !StaticCoreConfig.isConfigLoaded(tierType) ? 0 : StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableExtractionFilterSlots.get();
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, slots));
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cableComponent) {
		super.onAddedToCable(attachment, side, cableComponent);
		getAttachmentTag(attachment).putInt(EXTRACTION_TIMER_TAG, 0);
		getAttachmentTag(attachment).putBoolean(INPUT_BLOCKED, true);
	}

	@Override
	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		if (cable.getLevel().isClientSide || !cable.doesAttachmentPassRedstoneTest(attachment)) {
			return;
		}

		// Get the tile entity on the pulling side, return if it is null.
		BlockEntity te = cable.getLevel().getBlockEntity(cable.getPos().relative(side));
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
		IItemHandler filterItems = attachment.getCapability(ForgeCapabilities.ITEM_HANDLER)
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
		if (!getAttachmentTag(attachment).contains(EXTRACTION_TIMER_TAG)) {
			getAttachmentTag(attachment).putInt(EXTRACTION_TIMER_TAG, 0);
		}

		// Get the current timer and the extraction rate.
		int currentTimer = getAttachmentTag(attachment).getInt(EXTRACTION_TIMER_TAG);

		// Increment the current timer.
		currentTimer += 1;
		if (currentTimer >= StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableExtractorRate.get()) {
			getAttachmentTag(attachment).putInt(EXTRACTION_TIMER_TAG, 0);
			return true;
		} else {
			getAttachmentTag(attachment).putInt(EXTRACTION_TIMER_TAG, currentTimer);

			return false;
		}

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
	public ResourceLocation getModel(ItemStack attachment, BlockAndTintGetter level, BlockPos pos) {
		return model;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return StaticPowerRarities.getRarityForTier(this.tierType);
	}

	protected boolean performDigistoreExtract(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, BlockEntity targetTe) {
		if (targetTe instanceof BlockEntityDigistoreIOPort) {
			AtomicBoolean output = new AtomicBoolean(false);
			((BlockEntityDigistoreIOPort) targetTe).digistoreCableProvider.<DigistoreNetworkModule>getNetworkModule(ModCableModules.Digistore.get()).ifPresent(module -> {
				cable.<ItemNetworkModule>getNetworkModule(ModCableModules.Item.get()).ifPresent(network -> {
					// Return early if there is no manager.
					if (!module.isManagerPresent()) {
						return;
					}

					// Get the filter inventory (if there is a null value, do not handle it, throw
					// an exception).
					IItemHandler filterItems = attachment.getCapability(ForgeCapabilities.ITEM_HANDLER)
							.orElseThrow(() -> new RuntimeException("Encounetered an extractor attachment without a valid filter inventory."));

					// We do this to ensure we randomly extract.
					int startingSlot = SDMath.getRandomIntInRange(0, filterItems.getSlots() - 1);
					int currentSlot = startingSlot;

					// Get the list of filter items.
					for (int i = 0; i < filterItems.getSlots(); i++) {
						if (!filterItems.getStackInSlot(currentSlot).isEmpty()) {
							// Simulate an extract.
							ItemStack extractedItem = module.extractItem(filterItems.getStackInSlot(currentSlot),
									StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableExtractionStackSize.get(), true);

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
									StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableExtractedItemInitialSpeed.get());
							if (remainingAmount.getCount() < extractedItem.getCount()) {
								module.extractItem(extractedItem, extractedItem.getCount() - remainingAmount.getCount(), false);
								cable.getBlockEntity().setChanged();
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

	protected void performItemHandlerExtract(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, BlockEntity targetTe) {
		cable.<ItemNetworkModule>getNetworkModule(ModCableModules.Item.get()).ifPresent(network -> {
			// Attempt to extract an item.
			targetTe.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite()).ifPresent(inv -> {
				for (int i = 0; i < inv.getSlots(); i++) {
					// Simulate an extract.
					ItemStack extractedItem = inv.extractItem(i, StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableExtractionStackSize.get(), true);

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
							StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableExtractedItemInitialSpeed.get());
					if (remainingAmount.getCount() < extractedItem.getCount()) {
						inv.extractItem(i, extractedItem.getCount() - remainingAmount.getCount(), false);
						cable.getBlockEntity().setChanged();
						break;
					}
				}
			});
		});
	}

	protected void performFluidExtract(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, BlockEntity targetTe) {
		cable.<FluidNetworkModule>getNetworkModule(ModCableModules.Fluid.get()).ifPresent(network -> {
			FluidCableComponent fluidCable = (FluidCableComponent) cable;
			// Attempt to extract an item.
			targetTe.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()).ifPresent(tank -> {
				if (tank.getTanks() <= 0) {
					return;
				}
				if (fluidCable.isFluidValid(0, tank.getFluidInTank(0))) {
					FluidStack drained = tank.drain(StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableExtractionFluidRate.get(), FluidAction.SIMULATE);
					int filled = fluidCable.fill(drained, FluidAction.EXECUTE);
					tank.drain(filled, FluidAction.EXECUTE);
				}
			});
		});
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(Component.translatable("gui.staticpower.extractor_tooltip"));
		AttachmentTooltipUtilities.addSlotsCountTooltip("gui.staticpower.slots", StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableExtractionFilterSlots.get(),
				tooltip);
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		tooltip.add(Component.literal(""));
		tooltip.add(Component.translatable("gui.staticpower.extractor_rate_format",
				ChatFormatting.AQUA.toString() + StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableExtractorRate.get()));
		tooltip.add(Component.literal("� ").append(Component.translatable("gui.staticpower.extractor_stack_size",
				ChatFormatting.GOLD.toString() + StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableExtractionStackSize.get())));
		tooltip.add(Component.literal("� ").append(Component.translatable("gui.staticpower.extractor_fluid_rate", ChatFormatting.BLUE
				+ GuiTextUtilities.formatFluidToString(StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableExtractionFluidRate.get()).getString())));

		double blocksPerTick = StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableExtractedItemInitialSpeed.get();
		tooltip.add(Component.literal("� ").append(Component.translatable("gui.staticpower.cable_transfer_rate",
				ChatFormatting.GREEN + GuiTextUtilities.formatUnitRateToString(blocksPerTick).getString(), Component.translatable("gui.staticpower.blocks").getString())));
	}

	protected class ExtractorContainerProvider extends AbstractCableAttachmentContainerProvider {
		public ExtractorContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
			return new ContainerExtractor(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
