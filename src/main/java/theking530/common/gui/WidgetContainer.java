package theking530.common.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.AbstractGuiWidget;
import theking530.common.gui.widgets.AbstractGuiWidget.EInputResult;
import theking530.common.utilities.Vector2D;

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

	public void tick() {
		// Tick all the widgets.
		for (AbstractGuiWidget widget : widgets) {
			widget.tick();
		}
	}

	public void update(Vector2D ownerPosition, Vector2D ownerSize, float partialTicks, int mouseX, int mouseY) {
		// Render the foreground of all the widgets.
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.updateBeforeRender(ownerPosition, ownerSize, partialTicks, mouseX, mouseY);
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

	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		// Render the foreground of all the widgets.
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.renderBehindItems(mouseX, mouseY, partialTicks);
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
			if (widget.isVisible() && !widget.getTooltipsDisabled() && (!widget.getShouldAutoCalculateTooltipBounds() || (widget.getShouldAutoCalculateTooltipBounds() && widget.isPointInsideBounds(mousePosition)))) {
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
				widget.mouseMove((int) mouseX, (int) mouseY);
			}
		}
	}

	public EInputResult handleMouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		// Raise the mouse scrolled event for all the widgets,
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				if (widget.mouseScrolled(mouseX, mouseY, scrollDelta) == EInputResult.HANDLED) {
					return EInputResult.HANDLED;
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult handleMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		// Raise the mouse scrolled event for all the widgets,
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				if (widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) == EInputResult.HANDLED) {
					return EInputResult.HANDLED;
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult handleKeyPressed(int key, int scanCode, int modifiers) {
		// Raise the key presed event for all the widgets,
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				if (widget.keyPressed(key, scanCode, modifiers) == EInputResult.HANDLED) {
					return EInputResult.HANDLED;
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult characterTyped(char character, int p_charTyped_2_) {
		// Raise the character typed event for all the widgets,
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				if (widget.characterTyped(character, p_charTyped_2_) == EInputResult.HANDLED) {
					return EInputResult.HANDLED;
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public WidgetContainer registerWidget(AbstractGuiWidget widget) {
		widgets.add(widget);
		return this;
	}

	public Set<AbstractGuiWidget> getWidgets() {
		return widgets;
	}
}
