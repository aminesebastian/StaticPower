package theking530.staticcore.gui.widgets;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.IDrawable;
import theking530.staticcore.utilities.Vector2D;

public class DrawableWidget<T extends IDrawable> extends AbstractGuiWidget {
	private T drawable;
	private Component tooltip;
	private float zLevel;

	public DrawableWidget(float xPosition, float yPosition, float width, float height, T drawable) {
		super(xPosition, yPosition, width, height);
		this.drawable = drawable;
		this.drawable.setSize(width, height);
		this.zLevel = 0.0f;
	}

	public DrawableWidget(float xPosition, float yPosition, T drawable) {
		this(xPosition, yPosition, drawable.getSize().getX(), drawable.getSize().getY(), drawable);
	}

	@Override
	public AbstractGuiWidget setSize(float width, float height) {
		this.drawable.setSize(width, height);
		return super.setSize(width, height);
	}

	public DrawableWidget<T> setDrawable(T drawable) {
		this.drawable = drawable;
		return this;
	}

	public DrawableWidget<T> setTooltip(Component tooltip) {
		this.tooltip = tooltip;
		return this;
	}

	public T getDrawable() {
		return drawable;
	}

	public DrawableWidget<T> setZLevel(float zLevel) {
		this.zLevel = zLevel;
		return this;
	}

	public float getZLevel() {
		return zLevel;
	}

	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		// Get the screen space position and offset it by the scale to center the
		// entity.
		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());
		if (drawable != null) {
			drawable.draw(screenSpacePosition.getX(), screenSpacePosition.getY(), zLevel);
		}
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		super.getTooltips(mousePosition, tooltips, showAdvanced);
		if (tooltip != null) {
			tooltips.add(tooltip);
		}
	}
}