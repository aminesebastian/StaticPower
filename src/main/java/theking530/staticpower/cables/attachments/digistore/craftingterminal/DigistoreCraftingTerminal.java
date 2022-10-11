package theking530.staticpower.cables.attachments.digistore.craftingterminal;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractDigistoreTerminalAttachment;
import theking530.staticpower.client.StaticPowerAdditionalModels;

public class DigistoreCraftingTerminal extends AbstractDigistoreTerminalAttachment {

	public DigistoreCraftingTerminal() {
		super(StaticPowerAdditionalModels.CABLE_DIGISTORE_CRAFTING_TERMINAL_ATTACHMENT_ON, StaticPowerAdditionalModels.CABLE_DIGISTORE_CRAFTING_TERMINAL_ATTACHMENT);
	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getUIContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return new DigistoreCraftingTerminalContainerProvider(attachment, cable, attachmentSide);
	}

	protected class DigistoreCraftingTerminalContainerProvider extends AbstractCableAttachmentContainerProvider {
		public DigistoreCraftingTerminalContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
			return new ContainerDigistoreCraftingTerminal(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}

	@Override
	public double getPowerUsage(ItemStack attachment) {
		return 1;
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(Component.translatable("gui.staticpower.digistore_crafting_terminal_tooltip"));
	}
}
