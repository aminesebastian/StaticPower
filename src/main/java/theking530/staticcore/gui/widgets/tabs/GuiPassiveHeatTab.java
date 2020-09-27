package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent;

@OnlyIn(Dist.CLIENT)
public class GuiPassiveHeatTab extends GuiMachineHeatTab {

	public GuiPassiveHeatTab(HeatStorageComponent storage) {
		super(storage);
	}

	@Override
	public void updateData() {
		super.updateData();
		clear();
		addKeyValueTwoLiner(new StringTextComponent("Heating"), GuiTextUtilities.formatHeatRateToString(heatStorage.getStorage().getHeatPerTick()), TextFormatting.RED);
		addKeyValueTwoLiner(new StringTextComponent("Dissipating"), GuiTextUtilities.formatHeatRateToString(heatStorage.getStorage().getCooledPerTick()), TextFormatting.AQUA);
		addKeyValueTwoLiner(new StringTextComponent("Conductivity"), GuiTextUtilities.formatConductivityToString(heatStorage.getStorage().getConductivity()), TextFormatting.GREEN);
	}
}
