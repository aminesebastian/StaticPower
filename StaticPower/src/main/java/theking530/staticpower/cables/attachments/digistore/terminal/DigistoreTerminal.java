package theking530.staticpower.cables.attachments.digistore.terminal;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import theking530.staticcore.cablenetwork.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractDigistoreTerminalAttachment;
import theking530.staticpower.client.StaticPowerAdditionalModels;

public class DigistoreTerminal extends AbstractDigistoreTerminalAttachment {
	public DigistoreTerminal() {
		super( StaticPowerAdditionalModels.CABLE_DIGISTORE_TERMINAL_ATTACHMENT_ON, StaticPowerAdditionalModels.CABLE_DIGISTORE_TERMINAL_ATTACHMENT);
	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getUIContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return new DigistoreTerminalContainerProvider(attachment, cable, attachmentSide);
	}

	protected class DigistoreTerminalContainerProvider extends AbstractCableAttachmentContainerProvider {
		public DigistoreTerminalContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
			return new ContainerDigistoreTerminal(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}

	@Override
	public double getPowerUsage(ItemStack attachment) {
		return 2;
	}
	
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(Component.translatable("gui.staticpower.digistore_terminal_tooltip"));
	}
}
