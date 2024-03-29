package theking530.staticpower.cables.attachments.digistore.terminalbase;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.button.FakeSlotButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.utilities.MetricConverter;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot.DigistoreItemCraftableState;

@OnlyIn(Dist.CLIENT)
public class DigistoreSlotButton extends FakeSlotButton {

	/**
	 * Creates an item button using an item stack as the icon.
	 * 
	 * @param icon The itemstack to display.
	 * @param xPos The xPosition of the button.
	 * @param yPos The yPosition of the button.
	 */
	public DigistoreSlotButton(ItemStack icon, int xPos, int yPos, BiConsumer<StandardButton, MouseButton> onClicked) {
		super(icon, xPos, yPos, onClicked);
		setClickSoundEnabled(false);
	}

	@Override
	protected void drawButtonOverlay(PoseStack stack, int buttonLeft, int buttonTop) {
		if (isEnabled()) {
			// If enabled, draw the original fake slot.
			super.drawButtonOverlay(stack, buttonLeft, buttonTop);

			// If this slot is empty, do nothing.
			if (itemIcon.isEmpty()) {
				return;
			}

			// Move forward in the Z axis.
			stack.pushPose();
			stack.translate(0.0, 0.0, 260.0);

			// Check if this item is ONLY craftable (meaning, there are 0 in the system).
			if (DigistoreInventorySnapshot.getCraftableStateOfItem(itemIcon) == DigistoreItemCraftableState.ONLY_CRAFTABLE) {
				// Draw a string that says: "Craft".
				GuiDrawUtilities.drawString(stack, "Craft", 16, 15, 0.0f, 0.5f, SDColor.EIGHT_BIT_WHITE, true);
			} else {
				// Pass the itemstack count through the metric converter.
				MetricConverter count = new MetricConverter(itemIcon.getCount());

				// Draw the item count string manually.
				GuiDrawUtilities.drawString(stack, count.getValueAsString(true), 16, 15, 0.0f, 0.5f, SDColor.EIGHT_BIT_WHITE, true);
			}
			stack.popPose();
		} else {
			GuiDrawUtilities.drawRectangle(stack, 16, 16, 0, 0, 200, new SDColor(0.0f, 0.0f, 0.0f, 0.5f));
		}

	}
}
