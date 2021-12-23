package theking530.staticcore.gui.widgets.tabs;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabState;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.StaticPowerContainerGui;

import theking530.staticcore.gui.widgets.AbstractGuiWidget.EInputResult;

@OnlyIn(Dist.CLIENT)
public class GuiTabManager extends AbstractGuiWidget {

	private List<BaseGuiTab> registeredTabs;
	private BaseGuiTab initiallyOpenTab;
	protected final StaticPowerContainerGui<?> owningGui;

	public GuiTabManager(StaticPowerContainerGui<?> owningGui) {
		super(0.0f, 0.0f, 0.0f, 0.0f);
		registeredTabs = new ArrayList<BaseGuiTab>();
		this.owningGui = owningGui;
		this.setShouldAutoCalculateTooltipBounds(false);
	}

	public GuiTabManager registerTab(BaseGuiTab tab, boolean initiallyOpen) {
		if (!registeredTabs.contains(tab)) {
			registeredTabs.add(tab);
			tab.setManager(this);
			if (initiallyOpen) {
				setInitiallyOpenTab(tab);
			}
		}
		return this;
	}

	public GuiTabManager registerTab(BaseGuiTab tab) {
		return registerTab(tab, false);
	}

	public GuiTabManager removeTab(BaseGuiTab tab) {
		if (registeredTabs.contains(tab)) {
			registeredTabs.remove(tab);
		}
		return this;
	}

	public List<BaseGuiTab> getRegisteredTabs() {
		return registeredTabs;
	}

	public void setInitiallyOpenTab(BaseGuiTab tab) {
		initiallyOpenTab = tab;
		initiallyOpenTab.setTabState(TabState.OPEN);
	}

	public StaticPowerContainerGui<?> getOwningGui() {
		return owningGui;
	}

	public void tabClosed(BaseGuiTab tab) {
	}

	public void tabOpened(BaseGuiTab tab) {
	}

	public void tabOpening(BaseGuiTab tabIn) {
		for (BaseGuiTab tab : registeredTabs) {
			if (tab != tabIn && tab.getTabSide() == tabIn.getTabSide()) {
				tab.setTabState(TabState.CLOSED);
			}
		}
	}

	public void tabClosing(BaseGuiTab tab) {
	}

	@Override
	public void updateBeforeRender(PoseStack matrixStack, Vector2D ownerSize, float partialTicks, int mouseX, int mouseY) {
		super.updateBeforeRender(matrixStack, ownerSize, partialTicks, mouseX, mouseY);
		int tabPositionX = (int) (getPosition().getX() + getOwnerSize().getX() - 1 + getPosition().getX());
		int tabPositionY = (int) (getPosition().getY() + 10 + getPosition().getY());

		// Allocate lists for the left and right tabs.
		List<BaseGuiTab> leftTabs = new ArrayList<BaseGuiTab>();
		List<BaseGuiTab> rightTabs = new ArrayList<BaseGuiTab>();

		// Populuate those lists.
		for (BaseGuiTab tab : registeredTabs) {
			if (tab.getTabSide() == TabSide.RIGHT) {
				rightTabs.add(tab);
			} else {
				leftTabs.add(tab);
			}
		}

		// Calculate the maximum amount we can push a tab down.
		int maxOffset = (int) (getPosition().getY() + getOwnerSize().getY() - 25);

		// Iterate through the right tabs.
		for (int i = rightTabs.size() - 1; i >= 0; i--) {
			// Allocate the offset.
			int offset = 0;

			// Set the offset.
			if (i > 0) {
				for (int j = i - 1; j >= 0; j--) {
					offset += (int) rightTabs.get(j).getBounds().getHeight() - 24;
				}
			}

			// Calculate the final offset and clamp it to the max.
			int adjustedOffset = Math.min(tabPositionY + (i * 25) + offset, maxOffset);

			// Push a matrix for this tab's position.
			matrixStack.pushPose();
			matrixStack.translate(tabPositionX, adjustedOffset, 0);

			// Update the position.
			rightTabs.get(i).updateTabPosition(matrixStack, tabPositionX, adjustedOffset, partialTicks, mouseX, mouseY, Math.max(0, rightTabs.size() - i - 1));

			// Pop the matrix.
			matrixStack.popPose();
		}

		// Iterate through the left tabs.
		for (int i = leftTabs.size() - 1; i >= 0; i--) {
			// Allocate the offset.
			int offset = 0;

			// Set the offset.
			if (i > 0) {
				for (int j = i - 1; j >= 0; j--) {
					offset += (int) leftTabs.get(j).getBounds().getHeight() - 24;
				}
			}

			// Calculate the final offset and clamp it to the max.
			int adjustedOffset = Math.min(tabPositionY + (i * 25) + offset, maxOffset);

			// Push a matrix for this tab's position.
			matrixStack.pushPose();
			matrixStack.translate((int) (tabPositionX - getOwnerSize().getX() - leftTabs.get(i).tabWidth - 21), adjustedOffset, 0);

			// Update the position.
			leftTabs.get(i).updateTabPosition(matrixStack, (int) (tabPositionX - getOwnerSize().getX() - leftTabs.get(i).tabWidth - 21), adjustedOffset, partialTicks, mouseX, mouseY,
					Math.max(0, leftTabs.size() - i - 1));

			// Pop the matrix.
			matrixStack.popPose();
		}
	}

	@Override
	public void renderBackground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		// Render the tab backgrounds.
		for (BaseGuiTab tab : registeredTabs) {
			matrix.pushPose();
			matrix.translate(tab.xPosition, tab.yPosition, 0);
			// Draw the tab panel.
			tab.drawTabPanel(matrix, partialTicks);

			// If open, draw the tab background.
			if (tab.isOpen()) {
				tab.renderBackground(matrix, mouseX, mouseY, partialTicks);
			}
			matrix.popPose();
		}
	}

	@Override
	public void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		for (BaseGuiTab tab : registeredTabs) {
			if (tab.isOpen()) {
				matrix.pushPose();
				matrix.translate(tab.xPosition, tab.yPosition, 0);
				tab.renderBehindItems(matrix, mouseX, mouseY, partialTicks);
				matrix.popPose();
			}
		}
	}

	@Override
	public void renderForeground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		for (BaseGuiTab tab : registeredTabs) {
			matrix.pushPose();
			matrix.translate(tab.xPosition, tab.yPosition, 0);
			tab.renderForeground(matrix, mouseX, mouseY, partialTicks);
			matrix.popPose();
		}
	}

	@Override
	public void updateData() {
		for (BaseGuiTab tab : registeredTabs) {
			tab.updateData();
		}
	}

	@Override
	public EInputResult mouseClick(int mouseX, int mouseY, int button) {
		for (BaseGuiTab tab : registeredTabs) {
			EInputResult inputUsed = tab.mouseClick(getLastRenderMatrix(), mouseX, mouseY, button);
			if (inputUsed == EInputResult.HANDLED) {
				return EInputResult.HANDLED;
			}
		}
		return EInputResult.UNHANDLED;
	}

	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		// Iterate through all the tabs.
		for (BaseGuiTab tab : registeredTabs) {
			// If this tab is hovered is any way, we only consider this one for tooltips.
			if (tab.getBounds().isPointInBounds(mousePosition)) {
				// If we are hovering the tab icon, add the title tooltip.
				if (tab.getIconBounds().isPointInBounds(mousePosition)) {
					tooltips.add(new TextComponent(tab.getTitle()));
				}

				// Add any other tooltips.
				tab.getTooltips(mousePosition, tooltips, showAdvanced);
				break;
			}
		}
	}

	@Override
	public void mouseMove(int mouseX, int mouseY) {
		for (BaseGuiTab tab : registeredTabs) {
			if (tab.isOpen()) {
				tab.mouseHover(mouseX, mouseY);
			}
		}
	}
}
