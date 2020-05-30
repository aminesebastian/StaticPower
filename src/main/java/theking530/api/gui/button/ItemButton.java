package theking530.api.gui.button;

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
	public ItemButton(ItemStack icon, int width, int height, int xPos, int yPos, Consumer<BaseButton> onClicked) {
		super(width, height, xPos, yPos, onClicked);
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
	public ItemButton(Item icon, int width, int height, int xPos, int yPos, Consumer<BaseButton> onClicked) {
		this(new ItemStack(icon), width, height, xPos, yPos, onClicked);
	}

	/**
	 * Draws the button at the location defined at construction time.
	 */
	@Override
	protected void drawButtonOverlay() {
		if (!itemIcon.isEmpty()) {
			int buttonLeft = owningGui.getGuiLeft() + xPosition + 1;
			int buttonTop = owningGui.getGuiTop() + yPosition + 1;
			customRenderer.renderItemIntoGUI(itemIcon, buttonLeft + 1, buttonTop);
		}
	}
}
