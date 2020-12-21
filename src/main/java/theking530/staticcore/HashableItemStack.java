package theking530.staticcore;

import java.util.Objects;

import net.minecraft.item.ItemStack;

public class HashableItemStack {
	private ItemStack stack;

	public HashableItemStack(ItemStack stack) {
		this.stack = stack;
	}

	public ItemStack getStack() {
		return stack;
	}

	@Override
	public int hashCode() {
		return Objects.hash(stack.getItem(), stack.getTag());
	}
}
