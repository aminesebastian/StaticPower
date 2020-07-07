package theking530.staticpower.items.cableattachments.digistoreterminal;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.initialization.ModContainerTypes;

public class ContainerDigistoreTerminal extends AbstractContainerDigistoreTerminal<DigistoreTerminal> {

	public ContainerDigistoreTerminal(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistoreTerminal(int windowId, PlayerInventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(ModContainerTypes.DIGISTORE_TERMINAL, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}
}
