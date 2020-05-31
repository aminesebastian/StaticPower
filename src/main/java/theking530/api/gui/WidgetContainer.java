package theking530.api.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.widgets.AbstractGuiWidget;
import theking530.api.gui.widgets.AbstractGuiWidget.EInputResult;
import theking530.api.utilities.Vector2D;

/**
 * Interface that enables a class to contains and render widgets.
 * 
 * @author Amine Sebastian
 *
 */
public class WidgetContainer {
	protected final HashSet<AbstractGuiWidget> widgets;

	public WidgetContainer() {
		widgets = new HashSet<AbstractGuiWidget>();
	}

	public void update(Vector2D ownerPosition, Vector2D ownerSize) {
		// Render the foreground of all the widgets.
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.updateBeforeRender(ownerPosition, ownerSize);
			}
		}
	}

	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		// Render the foreground of all the widgets.
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.renderBackground(mouseX, mouseY, partialTicks);
			}
		}
	}

	public void renderForegound(int mouseX, int mouseY, float partialTicks) {
		// Render the foreground of all the widgets.
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.renderForeground(mouseX, mouseY, partialTicks);
			}
		}
	}

	public void renderTooltips(int mouseX, int mouseY) {
		// Capture all the tooltips for all the widgets. Skip any invisible widgets or
		// widgets that are not hovered.
		Vector2D mousePosition = new Vector2D(mouseX, mouseY);
		List<ITextComponent> tooltips = new ArrayList<ITextComponent>();
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible() && widget.isPointInsideBounds(mousePosition)) {
				widget.getTooltips(mousePosition, tooltips, false);
			}
		}

		// If there are any tooltips to render, render them.
		if (tooltips.size() > 0) {
			// Format them and then draw them.
			Minecraft.getInstance().currentScreen.renderTooltip(Lists.transform(tooltips, (ITextComponent comp) -> comp.getFormattedText()), mouseX, mouseY, Minecraft.getInstance().fontRenderer);
		}
	}

	public EInputResult handleMouseClick(double mouseX, double mouseY, int button) {
		// Raise the mouse hovered event for all the widgets,
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				if (widget.mouseClick((int) mouseX, (int) mouseY, button) == EInputResult.HANDLED) {
					return EInputResult.HANDLED;
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public void handleMouseMove(double mouseX, double mouseY) {
		// Raise the mouse hovered event for all the widgets,
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.mouseHover((int) mouseX, (int) mouseY);
			}
		}
	}

	public WidgetContainer registerWidget(AbstractGuiWidget widget) {
		widgets.add(widget);
		return this;
	}
}
