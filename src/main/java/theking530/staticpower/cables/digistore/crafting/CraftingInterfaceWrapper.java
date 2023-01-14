package theking530.staticpower.cables.digistore.crafting;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.cablenetwork.Cable;

public class CraftingInterfaceWrapper {
	private final ItemStack attachment;
	private final Direction attachmentSide;
	private final Cable cable;

	public CraftingInterfaceWrapper(ItemStack attachment, Direction attachmentSide, Cable cable) {
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

	public Cable getCable() {
		return cable;
	}
}
