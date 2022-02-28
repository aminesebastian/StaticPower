package theking530.staticcore.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.AbstractGuiWidget.EInputResult;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab;
import theking530.staticcore.utilities.Vector2D;

/**
 * Interface that enables a class to contains and render widgets.
 * 
 * @author Amine Sebastian
 *
 */
@OnlyIn(Dist.CLIENT)
public class WidgetContainer {
	protected final List<AbstractGuiWidget<?>> widgets;
	protected final WidgetParent parent;
	protected BiConsumer<PoseStack, Integer> transformer;

	public WidgetContainer(WidgetParent parent) {
		widgets = new ArrayList<AbstractGuiWidget<?>>();
		this.parent = parent;
	}

	public void setTransfomer(BiConsumer<PoseStack, Integer> transformer) {
		this.transformer = transformer;
	}

	public void tick() {
		// Tick all the widgets.
		for (AbstractGuiWidget<?> widget : widgets) {
			widget.tick();
		}
	}

	public void updateBeforeRender(PoseStack matrixStack, Vector2D ownerSize, float partialTicks, int mouseX, int mouseY) {
		// Render the foreground of all the widgets. We should NOT check visibility here
		// as widgets may drive their visibility in there.
		for (int i = 0; i < widgets.size(); i++) {
			AbstractGuiWidget<?> widget = widgets.get(i);
			if (transformer != null) {
				matrixStack.pushPose();
				transformer.accept(matrixStack, i);
			}
			widget.updateBeforeRender(matrixStack, ownerSize, partialTicks, mouseX, mouseY);
			if (transformer != null) {
				matrixStack.popPose();
			}
		}
	}

	public void renderBackground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// Render the foreground of all the widgets.
		for (int i = 0; i < widgets.size(); i++) {
			AbstractGuiWidget<?> widget = widgets.get(i);
			if (widget.isVisible()) {
				if (transformer != null) {
					matrixStack.pushPose();
					transformer.accept(matrixStack, i);
				}
				widget.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
				if (transformer != null) {
					matrixStack.popPose();
				}
			}
		}
	}

	public void renderBehindItems(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// Render the foreground of all the widgets.
		for (int i = 0; i < widgets.size(); i++) {
			AbstractGuiWidget<?> widget = widgets.get(i);
			if (widget.isVisible()) {
				if (transformer != null) {
					matrixStack.pushPose();
					transformer.accept(matrixStack, i);
				}
				widget.renderBehindItems(matrixStack, mouseX, mouseY, partialTicks);
				if (transformer != null) {
					matrixStack.popPose();
				}
			}
		}
	}

	public void renderForegound(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// Render the foreground of all the widgets.
		for (int i = 0; i < widgets.size(); i++) {
			AbstractGuiWidget<?> widget = widgets.get(i);
			if (widget.isVisible()) {
				if (transformer != null) {
					matrixStack.pushPose();
					transformer.accept(matrixStack, i);
				}
				widget.renderForeground(matrixStack, mouseX, mouseY, partialTicks);
				if (transformer != null) {
					matrixStack.popPose();
				}
			}
		}
	}

	@SuppressWarnings("resource")
	public void renderTooltips(PoseStack matrixStack, int mouseX, int mouseY) {
		// Capture all the tooltips for all the widgets. Skip any invisible widgets or
		// widgets that are not hovered.
		Vector2D mousePosition = new Vector2D(mouseX, mouseY);
		List<Component> tooltips = new ArrayList<Component>();
		getTooltips(mousePosition, tooltips, Screen.hasShiftDown());

		// If there are any tooltips to render, render them.
		if (tooltips.size() > 0) {
			// Format them and then draw them.
			if (Minecraft.getInstance().screen != null) {
				Minecraft.getInstance().screen.renderComponentTooltip(matrixStack, tooltips, mouseX, mouseY);
			}
		}
	}

	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		for (AbstractGuiWidget<?> widget : widgets) {
			if (widget.isVisible() && !widget.getTooltipsDisabled() && widget.isHovered()) {
				widget.getTooltips(mousePosition, tooltips, false);
			}
		}
	}

	public EInputResult handleMouseClick(double mouseX, double mouseY, int button) {
		// Raise the mouse hovered event for all the widgets,
		for (AbstractGuiWidget<?> widget : widgets) {
			if (widget.isVisible()) {
				if (widget.mouseClick((int) mouseX, (int) mouseY, button) == EInputResult.HANDLED) {
					return EInputResult.HANDLED;
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult handleMouseReleased(double mouseX, double mouseY, int button) {
		// Raise the mouse hovered event for all the widgets,
		for (AbstractGuiWidget<?> widget : widgets) {
			if (widget.isVisible()) {
				if (widget.mouseReleased((int) mouseX, (int) mouseY, button) == EInputResult.HANDLED) {
					return EInputResult.HANDLED;
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult handleMouseMove(double mouseX, double mouseY) {
		// Raise the mouse hovered event for all the widgets,
		for (AbstractGuiWidget<?> widget : widgets) {
			if (widget.isVisible()) {
				if (widget.mouseMove((int) mouseX, (int) mouseY) == EInputResult.HANDLED) {
					return EInputResult.HANDLED;
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult handleMouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		// Raise the mouse scrolled event for all the widgets,
		for (AbstractGuiWidget<?> widget : widgets) {
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
		for (AbstractGuiWidget<?> widget : widgets) {
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
		for (AbstractGuiWidget<?> widget : widgets) {
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
		for (AbstractGuiWidget<?> widget : widgets) {
			if (widget.isVisible()) {
				if (widget.characterTyped(character, p_charTyped_2_) == EInputResult.HANDLED) {
					return EInputResult.HANDLED;
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public WidgetContainer registerWidget(AbstractGuiWidget<?> widget) {
		widgets.add(widget);
		widget.setOwningContainer(this);
		widget.addedToParent(parent);
		return this;
	}

	public void clearWidgets() {
		for (AbstractGuiWidget<?> widget : widgets) {
			widget.removedFromParent(parent);
		}
		widgets.clear();
	}

	public boolean removeWidget(AbstractGuiWidget<?> widget) {
		widget.removedFromParent(parent);
		return widgets.remove(widget);
	}

	public List<AbstractGuiWidget<?>> getWidgets() {
		return Collections.unmodifiableList(widgets);
	}

	public WidgetParent getParent() {
		return this.parent;
	}

	public enum WidgetParentType {
		SCREEN, WIDGET, TAB
	}

	public static class WidgetParent {
		protected final Screen owningGui;
		protected final AbstractGuiWidget<?> owningWidget;
		protected final BaseGuiTab owningTab;
		protected final WidgetParentType type;

		protected WidgetParent(Screen owningGui, AbstractGuiWidget<?> owningWidget, BaseGuiTab owningTab, WidgetParentType type) {
			this.owningGui = owningGui;
			this.owningWidget = owningWidget;
			this.owningTab = owningTab;
			this.type = type;
		}

		public Screen getOwningGui() {
			return owningGui;
		}

		public AbstractGuiWidget<?> getOwningWidget() {
			return owningWidget;
		}

		public BaseGuiTab getOwningTab() {
			return owningTab;
		}

		public WidgetParentType getType() {
			return type;
		}

		// TODO: Populate this.
		public Vector2D getPosition() {
			if (type == WidgetParentType.WIDGET) {
				return owningWidget.getScreenSpacePosition();
			} else if (type == WidgetParentType.SCREEN) {
				return new Vector2D(0, 0);
			} else if (type == WidgetParentType.TAB) {
				return new Vector2D(0, 0);
			}
			return new Vector2D(0, 0);
		}

		public static WidgetParent fromScreen(Screen screen) {
			return new WidgetParent(screen, null, null, WidgetParentType.SCREEN);
		}

		public static WidgetParent fromWidget(AbstractGuiWidget<?> widget) {
			return new WidgetParent(null, widget, null, WidgetParentType.WIDGET);
		}

		public static WidgetParent fromTab(BaseGuiTab tab) {
			return new WidgetParent(null, null, tab, WidgetParentType.TAB);
		}
	}
}
