package theking530.common.gui.widgets.tabs;

import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.common.gui.GuiTextures;
import theking530.common.gui.drawables.ItemDrawable;
import theking530.common.utilities.Color;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent;

public class GuiMachineHeatTab extends AbstractInfoTab {
	protected HeatStorageComponent heatStorage;

	public GuiMachineHeatTab(HeatStorageComponent storage) {
		super("Heat I/O", 75, GuiTextures.ORANGE_TAB, new ItemDrawable(Items.CAMPFIRE), new Color(100, 255, 255));
		heatStorage = storage;
	}

	@Override
	public void updateData() {
		super.updateData();
		clear();
		addKeyValueTwoLiner(new StringTextComponent("Generating"), GuiTextUtilities.formatHeatRateToString(heatStorage.getStorage().getHeatPerTick()), TextFormatting.RED);
		addKeyValueTwoLiner(new StringTextComponent("Dissipating"), GuiTextUtilities.formatHeatRateToString(heatStorage.getStorage().getCooledPerTick()), TextFormatting.AQUA);
		addKeyValueTwoLiner(new StringTextComponent("Conductivity"), GuiTextUtilities.formatConductivityToString(heatStorage.getStorage().getConductivity()), TextFormatting.GREEN);
	}
}
