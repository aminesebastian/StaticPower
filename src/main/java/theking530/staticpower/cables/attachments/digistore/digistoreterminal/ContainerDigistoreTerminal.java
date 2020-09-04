package theking530.staticpower.cables.attachments.digistore.digistoreterminal;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.cables.AbstractCableProviderComponent;

public class ContainerDigistoreTerminal extends AbstractContainerDigistoreTerminal<DigistoreTerminal> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDigistoreTerminal, GuiDigistoreTerminal> TYPE = new ContainerTypeAllocator<>("digistore_terminal", ContainerDigistoreTerminal::new,
			GuiDigistoreTerminal::new);

	public ContainerDigistoreTerminal(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistoreTerminal(int windowId, PlayerInventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(TYPE, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}
}
