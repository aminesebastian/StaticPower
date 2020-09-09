package theking530.staticpower.cables.attachments.digistore.patternencoder;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractDigistoreTerminalAttachment;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.items.CableAttachmentInventoryCapabilityProvider;

public class DigistorePatternEncoder extends AbstractDigistoreTerminalAttachment {
	public enum RecipeEncodingType {
		CRAFTING_TABLE, MACHINE
	}

	public static final int PATTERN_INPUT_SLOT = 10;
	public static final int PATTERN_OUTPUT_SLOT = 11;
	public static final int RECIPE_START_SLOT = 0;
	public static final int RECIPE_OUTPUT_SLOT = 9;
	public static final int RECIPE_SINGLE_SLOT = 9;

	public DigistorePatternEncoder(String name) {
		super(name, StaticPowerAdditionalModels.CABLE_DIGISTORE_PATTERN_ENCODER_ATTACHMENT_ON, StaticPowerAdditionalModels.CABLE_DIGISTORE_PATTERN_ENCODER_ATTACHMENT);
	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return new DigistoreCraftingTerminalContainerProvider(attachment, cable, attachmentSide);
	}

	/**
	 * Add the crafting input capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new CableAttachmentInventoryCapabilityProvider(stack, 12, 0, nbt);
	}

	protected class DigistoreCraftingTerminalContainerProvider extends AbstractCableAttachmentContainerProvider {
		public DigistoreCraftingTerminalContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
			return new ContainerDigistorePatternEncoder(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
