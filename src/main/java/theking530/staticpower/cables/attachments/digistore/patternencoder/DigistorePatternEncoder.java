package theking530.staticpower.cables.attachments.digistore.patternencoder;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractDigistoreTerminalAttachment;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
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

	public DigistorePatternEncoder(String name) {
		super(name, StaticPowerAdditionalModels.CABLE_DIGISTORE_PATTERN_ENCODER_ATTACHMENT_ON, StaticPowerAdditionalModels.CABLE_DIGISTORE_PATTERN_ENCODER_ATTACHMENT);
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
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, 12));
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

	@Override
	public long getPowerUsage(ItemStack attachment, DigistoreCableProviderComponent cableComponent) {
		return 1000;
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		tooltip.add(new TranslationTextComponent("gui.staticpower.digistore_pattern_encoder"));
	}
}
