package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent;
import theking530.staticcore.gui.text.GuiTextUtilities;

@OnlyIn(Dist.CLIENT)
public class GuiPassiveHeatTab extends GuiMachineHeatTab {

	public GuiPassiveHeatTab(HeatStorageComponent storage) {
		super(storage);
	}

	@Override
	public void tick() {
		super.tick();
		clear();
		addKeyValueTwoLiner("Heating", Component.literal("Heating"), GuiTextUtilities.formatHeatRateToString(heatStorage.getHeatPerTick()), ChatFormatting.RED);
		addKeyValueTwoLiner("Cooling", Component.literal("Cooling"), GuiTextUtilities.formatHeatRateToString(heatStorage.getCooledPerTick()), ChatFormatting.AQUA);
		if (heatStorage.getConductivity() != 1) {
			addKeyValueTwoLiner("Conductivity", Component.literal("Conductivity"), GuiTextUtilities.formatConductivityToString(heatStorage.getConductivity()),
					ChatFormatting.GREEN);
		}
	}
}
