package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
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
		addKeyValueTwoLiner("Heating", new TextComponent("Heating"), GuiTextUtilities.formatHeatRateToString(heatStorage.getStorage().getHeatPerTick()), ChatFormatting.RED);
		addKeyValueTwoLiner("Dissipating", new TextComponent("Dissipating"), GuiTextUtilities.formatHeatRateToString(heatStorage.getStorage().getCooledPerTick()), ChatFormatting.AQUA);
		addKeyValueTwoLiner("Conductivity", new TextComponent("Conductivity"), GuiTextUtilities.formatConductivityToString(heatStorage.getStorage().getConductivity()),
				ChatFormatting.GREEN);
	}
}
