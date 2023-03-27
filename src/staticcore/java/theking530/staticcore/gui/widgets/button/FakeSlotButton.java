package theking530.staticcore.gui.widgets.button;

import java.util.List;
import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector2D;

@OnlyIn(Dist.CLIENT)
public class FakeSlotButton extends StandardButton {
	protected ItemStack itemIcon;

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
		this.setClickSoundEnabled(false);
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

	@SuppressWarnings("resource")
	@Override
	public void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		if (!itemIcon.isEmpty()) {
			// Do not ever show advanced tooltips here as those contain the registry name
			// and NBT tag count.
			// Not the same as StaticPower Advanced tooltips.
			tooltips.addAll(itemIcon.getTooltipLines(Minecraft.getInstance().player, Default.NORMAL));
		}
	}

	@Override
	protected void drawButton(PoseStack stack) {
		GuiDrawUtilities.drawSlot(stack, 16, 16, 0, 0, 0);
	}

	/**
	 * Draws the button at the location defined at construction time.
	 */
	@Override
	protected void drawButtonOverlay(PoseStack pose, int buttonLeft, int buttonTop) {
		// If the item is not empty, render it.
		if (!itemIcon.isEmpty()) {
			Vector2D size = getSize();
			int halfSizeX = size.getXi() / 2;
			int halfSizeY = size.getYi() / 2;
			GuiDrawUtilities.drawItem(pose, itemIcon, (halfSizeX - 9), (halfSizeY - 9), 10.0f);
		}

		// Render the hover effect.
		if (isHovered()) {
			GuiDrawUtilities.drawRectangle(pose, 16, 16, 0, 0, 250, new SDColor(1.0f, 1.0f, 1.0f, 0.5f));
		}
	}
}
