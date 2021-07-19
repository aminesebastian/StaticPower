package theking530.staticcore.gui.widgets.button;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;

@OnlyIn(Dist.CLIENT)
public class FakeSlotButton extends StandardButton {
	protected ItemStack itemIcon;
	protected final ItemRenderer customRenderer;

	/**
	 * Creates an item button using an item stack as the icon.
	 * 
	 * @param icon The itemstack to display.
	 * @param xPos The xPosition of the button.
	 * @param yPos The yPosition of the button.
	 */
	public FakeSlotButton(ItemStack icon, int xPos, int yPos, BiConsumer<StandardButton, MouseButton> onClicked) {
		super(xPos, yPos, 16, 16, onClicked);
		itemIcon = icon;
		customRenderer = Minecraft.getInstance().getItemRenderer();
	}

	/**
	 * Creates an item button using an item as the icon.
	 * 
	 * @param icon The icon to display.
	 * @param xPos The xPosition of the button.
	 * @param yPos The yPosition of the button.
	 */
	public FakeSlotButton(Item icon, int xPos, int yPos, BiConsumer<StandardButton, MouseButton> onClicked) {
		this(new ItemStack(icon), xPos, yPos, onClicked);
	}

	public void setItemStack(ItemStack stack) {
		this.itemIcon = stack;
	}

	protected void drawButton(MatrixStack stack, int transformedButtonLeft, int transformedButtonTop) {
		Vector2D position = this.getPosition();
		GuiDrawUtilities.drawSlot(stack, position.getX(), position.getY(), 16, 16, 0);
	}

	/**
	 * Draws the button at the location defined at construction time.
	 */
	@Override
	protected void drawButtonOverlay(MatrixStack stack, int buttonLeft, int buttonTop) {
		// If the item is not empty, render it.
		if (!itemIcon.isEmpty()) {
			Vector2D size = getSize();
			int halfSizeX = size.getXi() / 2;
			int halfSizeY = size.getYi() / 2;
			customRenderer.renderItemIntoGUI(itemIcon, (int) buttonLeft + (halfSizeX - 8), (int) buttonTop + (halfSizeY - 8));
		}

		// Render the ghost overlay.
		GuiDrawUtilities.drawColoredRectangle(buttonLeft, buttonTop, 16, 16, 1000, new Color(0.0f, 0.0f, 0.0f, 0.1f));

		// Render the hover effect.
		if (isHovered()) {
			GuiDrawUtilities.drawColoredRectangle(buttonLeft, buttonTop, 16, 16, 1000, new Color(1.0f, 0.8f, 0.1f, 0.5f));
		}
	}
}
