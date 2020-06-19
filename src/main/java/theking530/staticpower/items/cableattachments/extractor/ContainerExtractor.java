package theking530.staticpower.items.cableattachments.extractor;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.items.cableattachments.AbstractCableAttachmentContainer;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.cables.CableUtilities;

public class ContainerExtractor extends AbstractCableAttachmentContainer<ExtractorAttachment> {

	public ContainerExtractor(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getAttachmentItemStack(inv, data));
	}

	public ContainerExtractor(int windowId, PlayerInventory playerInventory, ItemStack owner) {
		super(ModContainerTypes.EXTRACTOR_CONTAINER, windowId, playerInventory, owner);
	}

	private static ItemStack getAttachmentItemStack(PlayerInventory inv, PacketBuffer data) {
		Direction attachmentSide = Direction.values()[data.readInt()];
		BlockPos cablePosition = data.readBlockPos();
		AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(inv.player.world, cablePosition);
		return cableComponent.getAttachment(attachmentSide);
	}
}
