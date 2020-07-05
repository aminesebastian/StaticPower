package theking530.common.gui.drawables;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;

public class ItemDrawable implements IDrawable {
	private final ItemStack itemStack;

	public ItemDrawable(@Nonnull IItemProvider item) {
		itemStack = new ItemStack(item);
	}

	public ItemDrawable(@Nonnull ItemStack stack) {
		itemStack = stack.copy();
	}

	@Override
	public void draw(float x, float y) {
		if (itemStack != null) {
			Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(itemStack, (int) x, (int) y);
		}
	}
}
