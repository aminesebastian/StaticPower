package theking530.staticpower.client.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import api.gui.BaseGuiTab;
import api.gui.BaseGuiTab.TabState;

public class GuiTabManager {
	private List<BaseGuiTab> TABS;
	
	public GuiTabManager() {
		TABS = new ArrayList<BaseGuiTab>();
	}	
	public void registerTab(BaseGuiTab tab) {
		TABS.add(tab);
		tab.setManager(this);
	}
	public void removeTab(BaseGuiTab tab) {
		TABS.remove(tab);
	}
	public void drawTabs(int tabPositionX, int tabPositionY, int screenWidth, int screenHeight, float partialTicks) {
		for(int i=TABS.size()-1; i >= 0; i--) {
			TABS.get(i).update(tabPositionX, tabPositionY + (i*25), partialTicks);
		}
	}
	public void tabClosed(BaseGuiTab tab) {
		
	}
	public void tabOpened(BaseGuiTab tab) {
		
	}
	public void tabOpening(BaseGuiTab tab) {
		for(int i=0; i<TABS.size(); i++) {
			if(TABS.get(i) != tab) {
				TABS.get(i).setTabState(TabState.CLOSED);
			}
		}
	}
	public void tabClosing(BaseGuiTab tab) {

	}
	public void handleMouseInteraction(int mouseX, int mouseY, int button) {
		for(int i=0; i<TABS.size(); i++) {
			TABS.get(i).mouseInteraction(mouseX, mouseY, button);
		}
	}
	public void handleKeyboardInteraction(char par1, int par2) {
		for(int i=0; i<TABS.size(); i++) {
			TABS.get(i).keyboardInteraction(par1, par2);
		}
	}
	public void handleMouseClickMove(int x, int y, int button, long time) {
		for(int i=0; i<TABS.size(); i++) {
			TABS.get(i).mouseClickMoveIntraction(i, i, i, time);
		}
	}
}
