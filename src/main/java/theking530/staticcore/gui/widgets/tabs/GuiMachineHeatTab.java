package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Items;
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
		super("Heat I/O", new Color(100, 255, 255), 75, GuiTextures.ORANGE_TAB, new ItemDrawable(Items.CAMPFIRE));
		heatStorage = storage;
	}

	@Override
	public void updateData() {
		super.updateData();
		clear();
		addKeyValueTwoLiner("Generating", new TextComponent("Generating"), GuiTextUtilities.formatHeatRateToString(heatStorage.getStorage().getHeatPerTick()), ChatFormatting.RED);
		addKeyValueTwoLiner("Dissipating", new TextComponent("Dissipating"), GuiTextUtilities.formatHeatRateToString(heatStorage.getStorage().getCooledPerTick()), ChatFormatting.AQUA);
		addKeyValueTwoLiner("Conductivity", new TextComponent("Conductivity"), GuiTextUtilities.formatConductivityToString(heatStorage.getStorage().getConductivity()),
				ChatFormatting.GREEN);
	}
}
