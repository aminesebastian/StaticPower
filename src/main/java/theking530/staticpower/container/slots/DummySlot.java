package theking530.staticpower.container.slots;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DummySlot extends Slot {

	public DummySlot(int index, int xPosition, int yPosition) {
		super(null, index, xPosition, yPosition);
	}

	@Override
	public void onQuickCraft(ItemStack oldStackIn, ItemStack newStackIn) {
	}

	@Override
	protected void onQuickCraft(ItemStack stack, int amount) {
	}

	@Override
	protected void onSwapCraft(int p_190900_1_) {
	}

	@Override
	protected void checkTakeAchievements(ItemStack stack) {
	}

	@Override
	public void onTake(Player thePlayer, ItemStack stack) {

	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getItem() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean hasItem() {
		return !this.getItem().isEmpty();
	}

	@Override
	public void set(ItemStack stack) {
	}

	@Override
	public void setChanged() {
	}

	@Override
	public int getMaxStackSize() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return this.getMaxStackSize();
	}

	@Override
	public ItemStack remove(int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public boolean isSameInventory(Slot other) {
		return true;
	}
}
