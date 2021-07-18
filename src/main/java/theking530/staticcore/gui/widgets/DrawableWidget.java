package theking530.staticcore.gui.widgets;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.IDrawable;
import theking530.staticcore.utilities.Vector2D;

public class DrawableWidget<T extends IDrawable> extends AbstractGuiWidget {
	private T drawable;
	private ITextComponent tooltip;

	public DrawableWidget(float xPosition, float yPosition, float width, float height, T drawable) {
		super(xPosition, yPosition, width, height);
		this.drawable = drawable;
		this.drawable.setSize(width, height);
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

	public DrawableWidget<T> setTooltip(ITextComponent tooltip) {
		this.tooltip = tooltip;
		return this;
	}

	public T getDrawable() {
		return drawable;
	}

	@Override
	public void renderBehindItems(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		// Get the screen space position and offset it by the scale to center the
		// entity.
		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());
		if (drawable != null) {
			drawable.draw(screenSpacePosition.getX(), screenSpacePosition.getY());
		}
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		super.getTooltips(mousePosition, tooltips, showAdvanced);
		if (tooltip != null) {
			tooltips.add(tooltip);
		}
	}
}