package theking530.staticpower.cables.attachments.digistore.terminalbase;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.button.FakeSlotButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot.DigistoreItemCraftableState;
import theking530.staticpower.utilities.MetricConverter;

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

			// Get the current position of the slot.
			Vector2D pos = getPosition();

			// Move forward in the Z axis.
			stack.pushPose();
			stack.translate(0.0, 0.0, 260.0);

			// Check if this item is ONLY craftable (meaning, there are 0 in the system).
			if (DigistoreInventorySnapshot.getCraftableStateOfItem(itemIcon) == DigistoreItemCraftableState.ONLY_CRAFTABLE) {
				// Draw a string that says: "Craft".
				GuiDrawUtilities.drawStringWithSize(stack, "Craft", pos.getX() + 16, pos.getY() + 15, 0.5f, Color.EIGHT_BIT_WHITE, true);
			} else {
				// Pass the itemstack count through the metric converter.
				MetricConverter count = new MetricConverter(itemIcon.getCount());

				// Draw the item count string manually.
				GuiDrawUtilities.drawStringWithSize(stack, count.getValueAsString(true), pos.getX() + 16, pos.getY() + 15, 0.5f, Color.EIGHT_BIT_WHITE, true);
			}
			stack.popPose();
		} else {
			GuiDrawUtilities.drawColoredRectangle(buttonLeft, buttonTop, 16, 16, 200, new Color(0.0f, 0.0f, 0.0f, 0.5f));
		}

	}
}
