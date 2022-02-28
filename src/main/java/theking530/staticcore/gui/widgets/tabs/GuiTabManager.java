package theking530.staticcore.gui.widgets.tabs;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabState;
import theking530.staticcore.utilities.Vector2D;

@OnlyIn(Dist.CLIENT)
public class GuiTabManager extends AbstractGuiWidget<GuiTabManager> {

	private List<BaseGuiTab> registeredTabs;
	private BaseGuiTab initiallyOpenTab;

	public GuiTabManager() {
		super(0.0f, 0.0f, 0.0f, 0.0f);
		registeredTabs = new ArrayList<BaseGuiTab>();
		this.setShouldAutoCalculateTooltipBounds(false);
	}

	public GuiTabManager registerTab(BaseGuiTab tab, boolean initiallyOpen) {
		if (!registeredTabs.contains(tab)) {
			registeredTabs.add(tab);
			tab.setManager(this);
			if (initiallyOpen) {
				setInitiallyOpenTab(tab);
			}
			registerWidget(tab);
		}
		return this;
	}

	public GuiTabManager registerTab(BaseGuiTab tab) {
		return registerTab(tab, false);
	}

	public GuiTabManager removeTab(BaseGuiTab tab) {
		if (registeredTabs.contains(tab)) {
			registeredTabs.remove(tab);
			removeWidget(tab);
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
	public void updateWidgetBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX, int mouseY) {
		int tabPositionX = (int) (getPosition().getX() + getParentSize().getX() - 1 + getPosition().getX());
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
		int maxOffset = (int) (getPosition().getY() + getParentSize().getY() - 25);

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
			BaseGuiTab tab = leftTabs.get(i);

			// Allocate the offset.
			int offset = 0;

			// Set the offset.
			if (i > 0) {
				for (int j = i - 1; j >= 0; j--) {
					offset += (int) leftTabs.get(j).getBounds().getHeight() - 24;
				}
			}

			// Calculate the final offset and clamp it to the max.
			int yOffset = Math.min(tabPositionY + (i * 25) + offset, maxOffset);

			// Push a matrix for this tab's position.
			matrixStack.pushPose();
			matrixStack.translate(0, yOffset, 0);

			// Update the position.
			tab.updateTabPosition(matrixStack, -tab.getWidth(), yOffset, partialTicks, mouseX, mouseY, Math.max(0, leftTabs.size() - i - 1));

			// Pop the matrix.
			matrixStack.popPose();
		}
	}
}
