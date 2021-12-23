package theking530.staticcore.gui.widgets.button;

import java.util.List;
import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
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
		super(xPos, yPos, 18, 18, onClicked);
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

	public ItemStack getItemStack() {
		return itemIcon;
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		if(!itemIcon.isEmpty()) {
			tooltips.addAll(itemIcon.getTooltipLines(Minecraft.getInstance().player, showAdvanced ? Default.ADVANCED : Default.NORMAL));	
		}
	}

	protected void drawButton(PoseStack stack, int transformedButtonLeft, int transformedButtonTop) {
		Vector2D position = this.getPosition();
		GuiDrawUtilities.drawSlot(stack, position.getX(), position.getY(), 16, 16, 0);
	}

	/**
	 * Draws the button at the location defined at construction time.
	 */
	@Override
	protected void drawButtonOverlay(PoseStack stack, int buttonLeft, int buttonTop) {
		// If the item is not empty, render it.
		if (!itemIcon.isEmpty()) {
			Vector2D size = getSize();
			int halfSizeX = size.getXi() / 2;
			int halfSizeY = size.getYi() / 2;
			customRenderer.renderGuiItem(itemIcon, (int) buttonLeft + (halfSizeX - 9), (int) buttonTop + (halfSizeY - 9));
		}

		// Render the hover effect.
		if (isHovered()) {
			GuiDrawUtilities.drawColoredRectangle(buttonLeft, buttonTop, 16, 16, 200, new Color(1.0f, 1.0f, 1.0f, 0.5f));
		}
	}
}
