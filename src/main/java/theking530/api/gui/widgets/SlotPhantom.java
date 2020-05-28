package theking530.api.gui.widgets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import theking530.api.container.StaticPowerContainerSlot;

public class SlotPhantom extends StaticPowerContainerSlot {
	private ItemStackHandler itemHandler;

	public SlotPhantom(ItemStackHandler inv, int index, int x, int y) {
		super(inv, index, x, y);
		itemHandler = inv;
	}

	@Override
	public boolean canTakeStack(PlayerEntity player) {
		super.canTakeStack(player);
		itemHandler.setStackInSlot(slotNumber, ItemStack.EMPTY);
		return false;
	}

	public boolean isItemValid(ItemStack itemStack) {
		ItemStack tempItemStack = itemStack.copy();
		tempItemStack.setCount(1);
		itemHandler.setStackInSlot(slotNumber, tempItemStack);
		return false;
	}
}
