package theking530.api.gui.widgets.tabs;

import net.minecraft.util.text.TextFormatting;
import theking530.staticpower.tileentities.utilities.interfaces.IEnergyUser;

public class GuiMachinePowerInfoTab extends GuiPowerInfoTab {

	private IEnergyUser energyUser;

	public GuiMachinePowerInfoTab(int width, int height, IEnergyUser machine) {
		super(machine.getEnergyStorage());
		energyUser = machine;
	}

	@Override
	protected void setPowerInfoText() {
		String text = (TextFormatting.GREEN + "Current I/O: =" + energyUser.getEnergyStorage().getEnergyIO() + " RF/t=" + TextFormatting.AQUA + "Max Recieve:=" + energyUser.getEnergyStorage().getStorage().getMaxReceive() + " RF/t="
				+ TextFormatting.RED + "Max Usage:=" + energyUser.maxEnergyUsagePerTick()) + " RF/t";
		setText("Power Usage", text);
	}
}
