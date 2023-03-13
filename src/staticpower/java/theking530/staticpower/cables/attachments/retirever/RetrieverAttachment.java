package theking530.staticpower.cables.attachments.retirever;

import java.util.List;

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
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.utilities.StaticPowerRarities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.attachments.AttachmentTooltipUtilities;
import theking530.staticpower.cables.item.ItemNetworkModule;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.utilities.InventoryUtilities;

public class RetrieverAttachment extends AbstractCableAttachment {
	public static final String RETRIEVEAL_TIMER_TAG = "retrieval_timer";
	private final ResourceLocation model;
	private final ResourceLocation tierType;

	public RetrieverAttachment(ResourceLocation tierType, ResourceLocation model) {
		super();
		this.model = model;
		this.tierType = tierType;
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		int slots = !StaticPowerConfig.isConfigLoaded(tierType) ? 0 : StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableRetrievalFilterSlots.get();
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, slots));
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		super.onAddedToCable(attachment, side, cable);
		getAttachmentTag(attachment).putInt(RETRIEVEAL_TIMER_TAG, 0);
	}

	@Override
	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		if (cable.getLevel().isClientSide || !cable.doesAttachmentPassRedstoneTest(attachment)) {
			return;
		}

		// Increment the retrieval timer. If it returns true, continue. If not, stop.
		if (!incrementRetrievalTimer(attachment)) {
			return;
		}

		// Get the tile entity on the inserting side, return if it is null.
		BlockEntity adjacentEntity = cable.getLevel().getBlockEntity(cable.getPos().relative(side));
		if (adjacentEntity == null || adjacentEntity.isRemoved()) {
			return;
		}

		// Get the filter inventory.
		IItemHandler filterInventory = attachment.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);

		// Get the filter inventory.
		IItemHandler targetInventory = adjacentEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite()).orElse(null);

		// Attempt to retrieve the item.
		cable.<ItemNetworkModule>getNetworkModule(ModCableModules.Item.get()).ifPresent(network -> {
			for (int i = 0; i < filterInventory.getSlots(); i++) {
				ItemStack filterItem = InventoryUtilities.getRandomItemStackFromInventory(filterInventory, 1, true);
				if (filterItem.isEmpty()) {
					continue;
				}

				// Check to make sure the destination can accept the item.
				if (!InventoryUtilities.canPartiallyInsertItemIntoInventory(targetInventory, filterItem)) {
					continue;
				}

				// If we're able to retrieve an item, break.
				if (network.retrieveItemStack(filterItem, StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableRetrievalStackSize.get(),
						cable.getPos().relative(side), side, StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableRetrievedItemInitialSpeed.get())) {
					break;
				}
			}
		});
	}

	public boolean incrementRetrievalTimer(ItemStack attachment) {
		if (!attachment.hasTag()) {
			attachment.setTag(new CompoundTag());
		}
		if (!getAttachmentTag(attachment).contains(RETRIEVEAL_TIMER_TAG)) {
			getAttachmentTag(attachment).putInt(RETRIEVEAL_TIMER_TAG, 0);
		}

		// Get the current timer and the extraction rate.
		int currentTimer = getAttachmentTag(attachment).getInt(RETRIEVEAL_TIMER_TAG);

		// Increment the current timer.
		currentTimer += 1;
		if (currentTimer >= StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableRetrievalRate.get()) {
			getAttachmentTag(attachment).putInt(RETRIEVEAL_TIMER_TAG, 0);
			return true;
		} else {
			getAttachmentTag(attachment).putInt(RETRIEVEAL_TIMER_TAG, currentTimer);

			return false;
		}
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
		tooltip.add(Component.translatable("gui.staticpower.retriever_tooltip"));
		AttachmentTooltipUtilities.addSlotsCountTooltip("gui.staticpower.slots", StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableRetrievalFilterSlots.get(),
				tooltip);
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		tooltip.add(Component.literal(""));
		tooltip.add(Component.translatable("gui.staticpower.retriever_rate_format",
				ChatFormatting.AQUA.toString() + StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableRetrievalRate.get()));
		tooltip.add(Component.literal("� ").append(Component.translatable("gui.staticpower.retriever_stack_size",
				ChatFormatting.GOLD.toString() + StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableRetrievalStackSize.get())));

		double blocksPerTick = StaticPowerConfig.getTier(tierType).cableAttachmentConfiguration.cableRetrievedItemInitialSpeed.get();
		tooltip.add(Component.literal("� ").append(Component.translatable("gui.staticpower.cable_transfer_rate",
				ChatFormatting.GREEN + GuiTextUtilities.formatUnitRateToString(blocksPerTick).getString(), Component.translatable("gui.staticpower.blocks").getString())));
	}

	protected class FilterContainerProvider extends AbstractCableAttachmentContainerProvider {
		public FilterContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
			return new ContainerRetriever(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
