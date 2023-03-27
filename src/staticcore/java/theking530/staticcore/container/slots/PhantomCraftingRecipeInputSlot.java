package theking530.staticcore.container.slots;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PhantomCraftingRecipeInputSlot extends CraftingRecipeInputSlot {

	public PhantomCraftingRecipeInputSlot(Container inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPickup(Player player) {
		super.mayPickup(player);
		container.setItem(index, ItemStack.EMPTY);
		return false;
	}

	public boolean mayPlace(ItemStack itemStack) {
		ItemStack tempItemStack = itemStack.copy();
		tempItemStack.setCount(1);
		container.setItem(index, tempItemStack);
		return false;
	}
}
