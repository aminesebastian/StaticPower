package theking530.staticpower.client.container.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DummySlot extends Slot {

	public DummySlot(int index, int xPosition, int yPosition) {
		super(null, index, xPosition, yPosition);
	}

	@Override
	public void onSlotChange(ItemStack oldStackIn, ItemStack newStackIn) {
	}

	@Override
	protected void onCrafting(ItemStack stack, int amount) {
	}

	@Override
	protected void onSwapCraft(int p_190900_1_) {
	}

	@Override
	protected void onCrafting(ItemStack stack) {
	}

	@Override
	public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
		return stack;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getStack() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean getHasStack() {
		return !this.getStack().isEmpty();
	}

	@Override
	public void putStack(ItemStack stack) {
	}

	@Override
	public void onSlotChanged() {
	}

	@Override
	public int getSlotStackLimit() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return this.getSlotStackLimit();
	}

	@Override
	public ItemStack decrStackSize(int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canTakeStack(PlayerEntity playerIn) {
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isSameInventory(Slot other) {
		return true;
	}
}
