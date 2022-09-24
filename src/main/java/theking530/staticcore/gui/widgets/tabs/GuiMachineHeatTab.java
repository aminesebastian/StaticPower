package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.blockentities.components.heat.HeatStorageComponent;
import theking530.staticpower.client.utilities.GuiTextUtilities;

@OnlyIn(Dist.CLIENT)
public class GuiMachineHeatTab extends AbstractInfoTab {
	protected HeatStorageComponent heatStorage;

	public GuiMachineHeatTab(HeatStorageComponent storage) {
		super("Heat I/O", new Color(100, 255, 255), 105, new Color(1, 0.5f, 0.1f, 1.0f), new ItemDrawable(Items.CAMPFIRE));
		heatStorage = storage;
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
