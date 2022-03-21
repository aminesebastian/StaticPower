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
		this.internalContainer.setTransfomer(this::updateChildLayout);
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

	protected void updateChildLayout(PoseStack pose, AbstractGuiWidget<?> widget, int index) {

	}

	@Override
	public void updateWidgetBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX, int mouseY) {
		float maxOffset = getPosition().getY() + getParentSize().getY() - 25;
		int leftTabs = 0;
		int rightTabs = 0;
		float leftYOffset = 10;
		float rightYOffset = 10;

		for (BaseGuiTab tab : registeredTabs) {
			float xPosition = getPosition().getX() + getParentSize().getX() + getPosition().getX() - 2;
			float yPosition = rightYOffset;
			int index = rightTabs;

			if (tab.getTabSide() == TabSide.LEFT) {
				xPosition = -tab.getWidth()+2;
				yPosition = leftYOffset;
				index = leftTabs;
			}

			yPosition = Math.min(maxOffset, yPosition);

			// Push a matrix for this tab's position.
			matrixStack.pushPose();
			matrixStack.translate(xPosition, yPosition, 0);
			tab.updateTabPosition(matrixStack, xPosition, yPosition, partialTicks, mouseX, mouseY, index);
			matrixStack.popPose();

			if (tab.getTabSide() == TabSide.RIGHT) {
				rightTabs++;
				rightYOffset += tab.getHeight();
			} else {
				leftTabs++;
				leftYOffset += tab.getHeight();
			}
		}
	}
}
