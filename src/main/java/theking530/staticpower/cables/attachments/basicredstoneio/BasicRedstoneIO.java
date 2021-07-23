package theking530.staticpower.cables.attachments.basicredstoneio;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.client.StaticPowerAdditionalModels;

public class BasicRedstoneIO extends AbstractCableAttachment {

	public BasicRedstoneIO(String name) {
		super(name);
	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getUIContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return new FilterContainerProvider(attachment, cable, attachmentSide);
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return true;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		return StaticPowerAdditionalModels.CABLE_REDSTOND_BASIC_ATTACHMENT;
	}

	protected class FilterContainerProvider extends AbstractCableAttachmentContainerProvider {
		public FilterContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
			return new ContainerBasicRedstoneIO(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
