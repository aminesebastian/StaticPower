package theking530.staticpower.cables.attachments.digistore.terminal;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractDigistoreTerminalAttachment;
import theking530.staticpower.client.StaticPowerAdditionalModels;

import theking530.staticpower.cables.attachments.AbstractCableAttachment.AbstractCableAttachmentContainerProvider;

public class DigistoreTerminal extends AbstractDigistoreTerminalAttachment {
	public DigistoreTerminal(String name) {
		super(name, StaticPowerAdditionalModels.CABLE_DIGISTORE_TERMINAL_ATTACHMENT_ON, StaticPowerAdditionalModels.CABLE_DIGISTORE_TERMINAL_ATTACHMENT);
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
	public long getPowerUsage(ItemStack attachment) {
		return 2000;
	}
	
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(new TranslatableComponent("gui.staticpower.digistore_terminal_tooltip"));
	}
}
