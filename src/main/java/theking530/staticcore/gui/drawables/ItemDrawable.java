package theking530.staticcore.gui.drawables;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.Vector2D;

@OnlyIn(Dist.CLIENT)
public class ItemDrawable implements IDrawable {
	private ItemStack itemStack;
	private final Vector2D size;

	public ItemDrawable(@Nonnull ItemLike item) {
		itemStack = new ItemStack(item);
		size = new Vector2D(16, 16);
	}

	public ItemDrawable(@Nonnull ItemStack stack) {
		itemStack = stack.copy();
		size = new Vector2D(16, 16);
	}

	@Override
	public void draw(float x, float y, float z) {
		if (itemStack != null && !itemStack.isEmpty()) {
			float initialValue = Minecraft.getInstance().getItemRenderer().blitOffset;
			Minecraft.getInstance().getItemRenderer().blitOffset = z;

			Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(itemStack, (int) x, (int) y);

			Minecraft.getInstance().getItemRenderer().blitOffset = initialValue;
		}
	}

	public void setItemStack(ItemStack stack) {
		this.itemStack = stack;
	}

	@Override
	public void setSize(float width, float height) {

	}

	@Override
	public Vector2D getSize() {
		return size;
	}
}
