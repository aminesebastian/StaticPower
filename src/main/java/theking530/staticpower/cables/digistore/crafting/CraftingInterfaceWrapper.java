package theking530.staticpower.cables.digistore.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import theking530.staticpower.cables.network.ServerCable;

public class CraftingInterfaceWrapper {
	private final ItemStack attachment;
	private final Direction attachmentSide;
	private final ServerCable cable;

	public CraftingInterfaceWrapper(ItemStack attachment, Direction attachmentSide, ServerCable cable) {
		this.attachment = attachment;
		this.attachmentSide = attachmentSide;
		this.cable = cable;
	}

	public ItemStack getAttachment() {
		return attachment;
	}

	public Direction getAttachmentSide() {
		return attachmentSide;
	}

	public ServerCable getCable() {
		return cable;
	}
}
