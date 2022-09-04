package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
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
	public void tick() {
		super.tick();
		clear();
		addKeyValueTwoLiner("Heating", new TextComponent("Heating"), GuiTextUtilities.formatHeatRateToString(heatStorage.getHeatPerTick()), ChatFormatting.RED);
		addKeyValueTwoLiner("Cooling", new TextComponent("Cooling"), GuiTextUtilities.formatHeatRateToString(heatStorage.getCooledPerTick()), ChatFormatting.AQUA);
		if (heatStorage.getConductivity() != 1) {
			addKeyValueTwoLiner("Conductivity", new TextComponent("Conductivity"), GuiTextUtilities.formatConductivityToString(heatStorage.getConductivity()),
					ChatFormatting.GREEN);
		}
	}
}
