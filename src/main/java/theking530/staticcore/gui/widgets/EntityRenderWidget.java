package theking530.staticcore.gui.widgets;

import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;

public class EntityRenderWidget extends AbstractGuiWidget {
	private LivingEntity entity;

	public EntityRenderWidget(float xPosition, float yPosition, float width, float height, LivingEntity entity) {
		super(xPosition, yPosition, width, height);
		this.entity = entity;
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		// Calculate the scale.
		int scale = (int) (getSize().getX() + getSize().getY()) / 4;

		// Get the screen space position and offset it by the scale to center the
		// entity.
		Vector2D screenSpacePosition = getScreenSpacePosition();
		Vector2D offset = new Vector2D(screenSpacePosition.getX() + (getSize().getX() / 2.0f), screenSpacePosition.getY() + (getSize().getY() / 2.0f) + scale);

		// Draw the slot border.
		GuiDrawUtilities.drawSlot(screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY());

		// Then the background.
		GuiDrawUtilities.drawColoredRectangle(screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 1.0f, Color.BLACK);

		// And finally the entity.
		InventoryScreen.drawEntityOnScreen((int) offset.getX(), (int) offset.getY(), scale, (float) (offset.getX()) - mouseX, (float) (offset.getY()) - mouseY - scale, entity);
	}
}
