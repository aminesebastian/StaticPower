package theking530.common.gui.widgets.tabs;

import net.minecraft.util.text.TextFormatting;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent;

public class GuiPassiveHeatTab extends GuiMachineHeatTab {
	public GuiPassiveHeatTab(HeatStorageComponent storage) {
		super(storage);
	}

	@Override
	protected void updateHeatText() {
		info.clear();
		addInfoLine("Heating", GuiTextUtilities.formatHeatRateToString(heatStorage.getStorage().getHeatPerTick()), TextFormatting.RED);
		addInfoLine("Dissipating", GuiTextUtilities.formatHeatRateToString(heatStorage.getStorage().getCooledPerTick()), TextFormatting.AQUA);
		addInfoLine("Conductivity", GuiTextUtilities.formatConductivityToString(heatStorage.getStorage().getConductivity()), TextFormatting.GREEN);
	}
}
