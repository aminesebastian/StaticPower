package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.utilities.SDColor;

@OnlyIn(Dist.CLIENT)
public class GuiMachineHeatTab extends AbstractInfoTab {
	protected HeatStorageComponent heatStorage;

	public GuiMachineHeatTab(HeatStorageComponent storage) {
		super("Heat I/O", new SDColor(100, 255, 255), 105, new SDColor(1, 0.5f, 0.1f, 1.0f), new ItemDrawable(Items.CAMPFIRE));
		heatStorage = storage;
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
