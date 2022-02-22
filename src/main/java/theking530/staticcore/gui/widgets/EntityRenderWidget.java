package theking530.staticcore.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
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
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		// Calculate the scale.
		int scale = (int) (getSize().getX() + getSize().getY()) / 4;

		// Draw the slot border.
		GuiDrawUtilities.drawSlot(pose, getSize().getX(), getSize().getY(), 0, 0, 0);

		// Then the background.
		GuiDrawUtilities.drawRectangle(pose, getSize().getX(), getSize().getY(), 0, 0, 1.0f, Color.BLACK);

		// Get the screen space position and offset it by the scale to center the
		// entity.
		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(pose, new Vector2D(0, 0));
		Vector2D offset = new Vector2D(screenSpacePosition.getX() + (getSize().getX() / 2.0f), screenSpacePosition.getY() + (getSize().getY() / 2.0f) + scale);

		// And finally the entity.
		InventoryScreen.renderEntityInInventory((int) offset.getX(), (int) offset.getY(), scale, (float) (offset.getX()) - mouseX, (float) (offset.getY()) - mouseY - scale, entity);
	}
}
