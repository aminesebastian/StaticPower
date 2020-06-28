package theking530.common.gui.widgets.button;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
	public ItemButton(ItemStack icon, int xPos, int yPos, int width, int height, Consumer<StandardButton> onClicked) {
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
	public ItemButton(Item icon, int xPos, int yPos, int width, int height, Consumer<StandardButton> onClicked) {
		this(new ItemStack(icon), xPos, yPos, width, height, onClicked);
	}

	/**
	 * Draws the button at the location defined at construction time.
	 */
	@Override
	protected void drawButtonOverlay(int buttonLeft, int buttonTop) {
		if (!itemIcon.isEmpty()) {
			customRenderer.renderItemIntoGUI(itemIcon, (int) buttonLeft + 2, (int) buttonTop + 1);
		}
	}
}
