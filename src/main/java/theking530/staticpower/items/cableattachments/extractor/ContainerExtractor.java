package theking530.staticpower.items.cableattachments.extractor;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.items.cableattachments.AbstractCableAttachmentContainer;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;

public class ContainerExtractor extends AbstractCableAttachmentContainer<ExtractorAttachment> {

	public ContainerExtractor(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerExtractor(int windowId, PlayerInventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(ModContainerTypes.EXTRACTOR_CONTAINER, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}
}
