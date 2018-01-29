package theking530.staticpower.client.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import api.gui.BaseGuiTab;
import api.gui.BaseGuiTab.TabState;
import net.minecraft.client.gui.Gui;
import theking530.staticpower.client.gui.BaseGuiContainer;

public class GuiTabManager {
	
	private List<BaseGuiTab> registeredTabs;
	private BaseGuiTab initiallyOpenTab;
	private BaseGuiContainer owningGui;
	
	public GuiTabManager(BaseGuiContainer owner) {
		registeredTabs = new ArrayList<BaseGuiTab>();
		owningGui = owner;
	}	
	public void registerTab(BaseGuiTab tab) {
		registeredTabs.add(tab);
		tab.setManager(this);
	}
	public void removeTab(BaseGuiTab tab) {
		registeredTabs.remove(tab);
	}
	public List<BaseGuiTab> getRegisteredTabs() {
		return registeredTabs;
	}
	public void setInitiallyOpenTab(BaseGuiTab tab) {
		initiallyOpenTab = tab;
		tab.setTabState(TabState.OPEN);
	}
	public void drawTabs(int tabPositionX, int tabPositionY, int screenWidth, int screenHeight, float partialTicks) {
		for(int i=registeredTabs.size()-1; i >= 0; i--) {
			int offset = 0;
			if(i > 0) {
				for(int j=i-1; j >= 0; j--) {
					offset += (int) registeredTabs.get(j).getBounds().getH()-24;
				}
			}
			offset = Math.min((i*25) + offset, owningGui.getXSize()-(registeredTabs.size()*22));
			registeredTabs.get(i).update(tabPositionX, tabPositionY + offset, partialTicks);
		}
	}
	public void tabClosed(BaseGuiTab tab) {}
	public void tabOpened(BaseGuiTab tab) {}
	public void tabOpening(BaseGuiTab tab) {
		for(int i=0; i<registeredTabs.size(); i++) {
			if(registeredTabs.get(i) != tab) {
				registeredTabs.get(i).setTabState(TabState.CLOSED);
			}
		}
	}
	public void tabClosing(BaseGuiTab tab) {}
	public void handleMouseInteraction(int mouseX, int mouseY, int button) {
		for(int i=0; i<registeredTabs.size(); i++) {
			registeredTabs.get(i).mouseInteraction(mouseX, mouseY, button);
		}
	}
	public void handleKeyboardInteraction(char par1, int par2) {
		for(int i=0; i<registeredTabs.size(); i++) {
			registeredTabs.get(i).keyboardInteraction(par1, par2);
		}
	}
	public void handleMouseClickMove(int x, int y, int button, long time) {
		for(int i=0; i<registeredTabs.size(); i++) {
			registeredTabs.get(i).mouseClickMoveIntraction(i, i, i, time);
		}
	}
}
