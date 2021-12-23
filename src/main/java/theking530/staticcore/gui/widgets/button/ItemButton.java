package theking530.staticcore.gui.widgets.button;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.Vector2D;

@OnlyIn(Dist.CLIENT)
public class ItemButton extends StandardButton {

	protected ItemStack itemIcon;
	protected final ItemRenderer customRenderer;

	/**
	 * Creates an item button using an item stack as the icon.
	 * 
	 * @param icon   The itemstack to display.
	 * @param width  The width of the button.
	 * @param height The height of the button.
	 * @param xPos   The xPosition of the button.
	 * @param yPos   The yPosition of the button.
	 */
	public ItemButton(ItemStack icon, int xPos, int yPos, int width, int height, BiConsumer<StandardButton, MouseButton> onClicked) {
		super(xPos, yPos, width, height, onClicked);

		itemIcon = icon;
		customRenderer = Minecraft.getInstance().getItemRenderer();
	}

	/**
	 * Creates an item button using an item as the icon.
	 * 
	 * @param icon   The icon to display.
	 * @param width  The width of the button.
	 * @param height The height of the button.
	 * @param xPos   The xPosition of the button.
	 * @param yPos   The yPosition of the button.
	 */
	public ItemButton(Item icon, int xPos, int yPos, int width, int height, BiConsumer<StandardButton, MouseButton> onClicked) {
		this(new ItemStack(icon), xPos, yPos, width, height, onClicked);
	}

	public ItemButton setItemStack(ItemStack stack) {
		this.itemIcon = stack;
		return this;
	}

	public ItemButton setItem(Item item) {
		this.itemIcon = new ItemStack(item);
		return this;
	}

	public ItemStack getItemStack() {
		return itemIcon;
	}

	/**
	 * Draws the button at the location defined at construction time.
	 */
	@Override
	protected void drawButtonOverlay(PoseStack stack, int buttonLeft, int buttonTop) {
		if (!itemIcon.isEmpty()) {
			Vector2D size = this.getSize();
			int halfSizeX = size.getXi() / 2;
			int halfSizeY = size.getYi() / 2;
			customRenderer.renderGuiItem(itemIcon, (int) buttonLeft + (halfSizeX - 8), (int) buttonTop + (halfSizeY - 8));
		}
	}
}
