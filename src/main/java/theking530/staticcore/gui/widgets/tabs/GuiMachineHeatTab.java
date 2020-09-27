package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent;

@OnlyIn(Dist.CLIENT)
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
