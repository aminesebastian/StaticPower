package theking530.common.gui.widgets.tabs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import theking530.common.gui.widgets.AbstractGuiWidget;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabState;
import theking530.common.utilities.Vector2D;
import theking530.staticpower.client.gui.StaticPowerContainerGui;

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
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		Vector2D positionOffset = this.getPosition();
		int tabPositionX = (int) (getOwnerPosition().getX() + getOwnerSize().getX() - 1 + positionOffset.getX());
		int tabPositionY = (int) (getOwnerPosition().getY() + 10 + positionOffset.getY());

		List<BaseGuiTab> leftTabs = new ArrayList<BaseGuiTab>();
		List<BaseGuiTab> rightTabs = new ArrayList<BaseGuiTab>();

		for (BaseGuiTab tab : registeredTabs) {
			if (tab.getTabSide() == TabSide.RIGHT) {
				rightTabs.add(tab);
			} else {
				leftTabs.add(tab);
			}
		}

		int maxOffset = (int) (getOwnerPosition().getY() + getOwnerSize().getY() - 25);

		for (int i = rightTabs.size() - 1; i >= 0; i--) {
			int offset = 0;
			if (i > 0) {
				for (int j = i - 1; j >= 0; j--) {
					offset += (int) rightTabs.get(j).getBounds().getHeight() - 24;
				}
			}
			int adjustedOffset = Math.min(tabPositionY + (i * 25) + offset, maxOffset);
			rightTabs.get(i).updateTabPosition(tabPositionX, adjustedOffset, partialTicks, mouseX, mouseY, Math.max(0, rightTabs.size() - i - 1));
			rightTabs.get(i).drawTabPanel(partialTicks);
			if (rightTabs.get(i).isOpen()) {
				rightTabs.get(i).renderBackground(mouseX, mouseY, partialTicks);
			}
		}

		for (int i = leftTabs.size() - 1; i >= 0; i--) {
			int offset = 0;
			if (i > 0) {
				for (int j = i - 1; j >= 0; j--) {
					offset += (int) leftTabs.get(j).getBounds().getHeight() - 24;
				}
			}
			int adjustedOffset = Math.min(tabPositionY + (i * 25) + offset, maxOffset);
			leftTabs.get(i).updateTabPosition((int) (tabPositionX - getOwnerSize().getX() - 21), adjustedOffset, partialTicks, mouseX, mouseY, Math.max(0, leftTabs.size() - i - 1));
			leftTabs.get(i).drawTabPanel(partialTicks);
			if (leftTabs.get(i).isOpen()) {
				leftTabs.get(i).renderBackground(mouseX, mouseY, partialTicks);
			}
		}
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		for (BaseGuiTab tab : registeredTabs) {
			if (tab.isOpen()) {
				tab.renderBehindItems(mouseX, mouseY, partialTicks);
			}
		}
	}

	@Override
	public void renderForeground(int mouseX, int mouseY, float partialTicks) {
		for (BaseGuiTab tab : registeredTabs) {
			tab.renderForeground(mouseX, mouseY, partialTicks);
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
			EInputResult inputUsed = tab.mouseClick(mouseX, mouseY, button);
			if (inputUsed == EInputResult.HANDLED) {
				return EInputResult.HANDLED;
			}
		}
		return EInputResult.UNHANDLED;
	}

	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		// Iterate through all the tabs.
		for (BaseGuiTab tab : registeredTabs) {
			// If this tab is hovered is any way, we only consider this one for tooltips.
			if (tab.getBounds().isPointInBounds(mousePosition)) {
				// If we are hovering the tab icon, add the title tooltip.
				if (tab.getIconBounds().isPointInBounds(mousePosition)) {
					tooltips.add(new StringTextComponent(tab.getTitle()));
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
