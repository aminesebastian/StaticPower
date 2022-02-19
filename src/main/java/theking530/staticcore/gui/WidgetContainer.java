package theking530.staticcore.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.AbstractGuiWidget.EInputResult;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.StaticPowerContainerGui;

/**
 * Interface that enables a class to contains and render widgets.
 * 
 * @author Amine Sebastian
 *
 */
@OnlyIn(Dist.CLIENT)
public class WidgetContainer {
	protected final HashSet<AbstractGuiWidget> widgets;
	@Nullable
	protected final StaticPowerContainerGui<?> owner;
	protected Vector2D ownerPosition;

	public WidgetContainer(StaticPowerContainerGui<?> owner) {
		widgets = new HashSet<AbstractGuiWidget>();
		this.owner = owner;
		this.ownerPosition = new Vector2D(0, 0);
	}

	public WidgetContainer() {
		this(null);
	}

	public void tick() {
		// Tick all the widgets.
		for (AbstractGuiWidget widget : widgets) {
			widget.updateData();
		}
	}

	public void update(PoseStack matrixStack, Vector2D ownerPosition, Vector2D ownerSize, float partialTicks, int mouseX, int mouseY) {
		// Update the owner position.
		this.ownerPosition = ownerPosition;

		// Translate so we draw relative to the owner now.
		matrixStack.pushPose();
		matrixStack.translate(ownerPosition.getX(), ownerPosition.getY(), 0);

		// Render the foreground of all the widgets.
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.updateBeforeRender(matrixStack, ownerSize, partialTicks, mouseX, mouseY);
			}
		}

		// Pop the matrix when we're done.
		matrixStack.popPose();
	}

	public void renderBackground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// Translate so we draw relative to the owner now.
		matrixStack.pushPose();
		matrixStack.translate(ownerPosition.getX(), ownerPosition.getY(), 0);

		// Render the foreground of all the widgets.
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
			}
		}

		// Pop the matrix when we're done.
		matrixStack.popPose();
	}

	public void renderBehindItems(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// Translate so we draw relative to the owner now.
		matrixStack.pushPose();
		matrixStack.translate(ownerPosition.getX(), ownerPosition.getY(), 0);

		// Render the foreground of all the widgets.
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.renderBehindItems(matrixStack, mouseX, mouseY, partialTicks);
			}
		}

		// Pop the matrix when we're done.
		matrixStack.popPose();
	}

	public void renderForegound(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// Translate so we draw relative to the owner now.
		matrixStack.pushPose();
		matrixStack.translate(ownerPosition.getX(), ownerPosition.getY(), 0);

		// Render the foreground of all the widgets.
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.renderForeground(matrixStack, mouseX, mouseY, partialTicks);
			}
		}

		// Pop the matrix when we're done.
		matrixStack.popPose();
	}

	public void renderTooltips(PoseStack matrixStack, int mouseX, int mouseY) {
		// Translate so we draw relative to the owner now.
		matrixStack.pushPose();
		matrixStack.translate(ownerPosition.getX(), ownerPosition.getY(), 0);

		// Capture all the tooltips for all the widgets. Skip any invisible widgets or
		// widgets that are not hovered.
		Vector2D mousePosition = new Vector2D(mouseX, mouseY);
		List<Component> tooltips = new ArrayList<Component>();
		getTooltips(mousePosition, tooltips, Screen.hasShiftDown());

		// Pop the matrix when we're done.
		matrixStack.popPose();

		// If there are any tooltips to render, render them.
		if (tooltips.size() > 0) {
			// Format them and then draw them.
			if (Minecraft.getInstance().screen != null) {
				Minecraft.getInstance().screen.renderComponentTooltip(matrixStack, tooltips, mouseX, mouseY);
			}
		}
	}

	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		for (AbstractGuiWidget widget : widgets) {
			if (widget.isVisible() && !widget.getTooltipsDisabled()
					&& (!widget.getShouldAutoCalculateTooltipBounds() || (widget.getShouldAutoCalculateTooltipBounds() && widget.isPointInsideBounds(mousePosition)))) {
				widget.getTooltips(mousePosition, tooltips, false);
			}
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
		widget.setOwningContainer(this);
		if (owner != null) {
			widget.addedToGui(owner);
		}
		return this;
	}

	public boolean removeWidget(AbstractGuiWidget widget) {
		if (owner != null) {
			widget.removedFromGui(owner);
		}
		return widgets.remove(widget);
	}

	public Set<AbstractGuiWidget> getWidgets() {
		return widgets;
	}
}
