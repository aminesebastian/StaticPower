package api.gui.button;

import java.util.ArrayList;
import java.util.List;

import api.gui.IInteractableGui;

public class ButtonManager {

	private List<BaseButton> buttonList;
	private IInteractableGui owningGui;
	
	public ButtonManager(IInteractableGui owningGui) {
		buttonList = new ArrayList<BaseButton>();
		this.owningGui = owningGui;
	}
	public void registerButton(BaseButton button) {
		buttonList.add(button);
	}
	public void drawButtons(int mouseX, int mouseY) {
		for(int i=0; i<buttonList.size(); i++) {
			if(buttonList.get(i).isVisible()) {
				buttonList.get(i).handleMouseMoveInteraction(mouseX, mouseY);
			}
		}
		for(int i=0; i<buttonList.size(); i++) {
			if(buttonList.get(i).isVisible()) {
				buttonList.get(i).draw();
			}
		}
		for(int i=0; i<buttonList.size(); i++) {
			if(buttonList.get(i).isHovered()) {
				buttonList.get(i).drawTooltip();
			}
		}
		serviceButtons();
	}
	public void serviceButtons() {
		for(int i=0; i<buttonList.size(); i++) {
			if(buttonList.get(i).isClicked()) {
				owningGui.buttonPressed(buttonList.get(i));
				buttonList.get(i).setClicked(false);
			}
		}
	}
	public void handleMouseInteraction(int mouseX, int mouseY, int button) {
		for(int i=0; i<buttonList.size(); i++) {
			if(buttonList.get(i).isVisible()) {
				buttonList.get(i).handleMouseInteraction(mouseX, mouseY, button);
			}
		}
	}
}
