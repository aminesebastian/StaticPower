package theking530.common.gui.widgets.button;

import java.util.function.BiConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import theking530.common.utilities.Vector2D;

public class ItemButton extends StandardButton {

	protected final ItemStack itemIcon;
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

	/**
	 * Draws the button at the location defined at construction time.
	 */
	@Override
	protected void drawButtonOverlay(int buttonLeft, int buttonTop) {
		if (!itemIcon.isEmpty()) {
			Vector2D size = this.getSize();
			int halfSizeX = size.getXi() / 2;
			int halfSizeY = size.getYi() / 2;
			customRenderer.renderItemIntoGUI(itemIcon, (int) buttonLeft + (halfSizeX - 8), (int) buttonTop + (halfSizeY - 8));
		}
	}
}
