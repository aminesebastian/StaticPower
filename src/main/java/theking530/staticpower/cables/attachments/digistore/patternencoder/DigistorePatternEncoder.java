package theking530.staticpower.cables.attachments.digistore.patternencoder;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractDigistoreTerminalAttachment;
import theking530.staticpower.client.StaticPowerAdditionalModels;

public class DigistorePatternEncoder extends AbstractDigistoreTerminalAttachment {
	public enum RecipeEncodingType {
		CRAFTING_TABLE, MACHINE
	}

	public static final int PATTERN_INPUT_SLOT = 10;
	public static final int PATTERN_OUTPUT_SLOT = 11;
	public static final int RECIPE_START_SLOT = 0;
	public static final int RECIPE_OUTPUT_SLOT = 9;
	public static final int RECIPE_SINGLE_SLOT = 9;

	public DigistorePatternEncoder() {
		super(StaticPowerAdditionalModels.CABLE_DIGISTORE_PATTERN_ENCODER_ATTACHMENT_ON, StaticPowerAdditionalModels.CABLE_DIGISTORE_PATTERN_ENCODER_ATTACHMENT);
	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getUIContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return new DigistoreCraftingTerminalContainerProvider(attachment, cable, attachmentSide);
	}

	/**
	 * Add the crafting input capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, 12));
	}

	protected class DigistoreCraftingTerminalContainerProvider extends AbstractCableAttachmentContainerProvider {
		public DigistoreCraftingTerminalContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
			return new ContainerDigistorePatternEncoder(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}

	@Override
	public double getPowerUsage(ItemStack attachment) {
		return 1;
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(new TranslatableComponent("gui.staticpower.digistore_pattern_encoder"));
	}
}
