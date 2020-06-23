package theking530.staticpower.client.container.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class PhantomSlot extends StaticPowerContainerSlot {
	private ItemStackHandler itemHandler;

	public PhantomSlot(ItemStackHandler inv, int index, int x, int y) {
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
