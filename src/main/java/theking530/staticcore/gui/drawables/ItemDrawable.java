package theking530.staticcore.gui.drawables;

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
	public void draw(float x, float y, float z) {
		if (itemStack != null) {
			float initialValue = Minecraft.getInstance().getItemRenderer().zLevel;
			Minecraft.getInstance().getItemRenderer().zLevel = z - 50.0f;

			Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(itemStack, (int) x, (int) y);

			Minecraft.getInstance().getItemRenderer().zLevel = initialValue;
		}
	}
}
