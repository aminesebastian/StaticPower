package theking530.staticpower.client.gui.widgets.valuebars;

import java.util.List;

import theking530.staticpower.machines.BaseMachine;

public class GuiPowerBarFromEnergyStorage {

	GuiPowerBar POWER_BAR = new GuiPowerBar();
	private BaseMachine MACHINE;

	
	public GuiPowerBarFromEnergyStorage(BaseMachine teInfuser) {
		MACHINE = teInfuser;
	}
	
	public List<String> drawText() {
		if(MACHINE.processingTime == 0) {
			return POWER_BAR.drawText(MACHINE.energyStorage.getEnergyStored(), MACHINE.energyStorage.getMaxEnergyStored(), MACHINE.energyStorage.getMaxReceive(), 0);
		}
		return POWER_BAR.drawText(MACHINE.energyStorage.getEnergyStored(), MACHINE.energyStorage.getMaxEnergyStored(), MACHINE.energyStorage.getMaxReceive(), MACHINE.getProcessingCost()/MACHINE.processingTime);
	}
	public void drawPowerBar(int xpos, int ypos, int width, int height, float zLevel, float deltaTime) {
		POWER_BAR.drawPowerBar(xpos, ypos, width, height, zLevel, MACHINE.energyStorage.getEnergyStored(), MACHINE.energyStorage.getMaxEnergyStored(), deltaTime);
	}
}
