package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.heat.HeatUtilities;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.utilities.SDColor;

@OnlyIn(Dist.CLIENT)
public class GuiMachineHeatTab extends AbstractInfoTab {
	protected HeatStorageComponent heatStorage;

	public GuiMachineHeatTab(HeatStorageComponent storage) {
		super("Heat I/O", new SDColor(100, 255, 255), 105, new SDColor(1, 0.5f, 0.1f, 1.0f),
				new ItemDrawable(Items.CAMPFIRE));
		heatStorage = storage;
	}

	@Override
	public void tick() {
		super.tick();
		clear();

		MutableComponent heatingRateComponent;
		MutableComponent coolingRateComponent;
		if (isHovered()) {
			float heatingRatePerTick = HeatUtilities.calculateTemperatureDelta((float) heatStorage.getHeatPerTick(),
					heatStorage.getSpecificHeat(), heatStorage.getMass()) / 20;
			float coolingRatePerTick = HeatUtilities.calculateTemperatureDelta((float) heatStorage.getCooledPerTick(),
					heatStorage.getSpecificHeat(), heatStorage.getMass()) / 20;

			heatingRateComponent = GuiTextUtilities.formatHeatRateToString(heatingRatePerTick);
			coolingRateComponent = GuiTextUtilities.formatHeatRateToString(coolingRatePerTick);
		} else {
			heatingRateComponent = PowerTextFormatting.formatPowerRateToString(heatStorage.getHeatPerTick());
			coolingRateComponent = PowerTextFormatting.formatPowerRateToString(heatStorage.getCooledPerTick());
		}
		addKeyValueTwoLiner("Heating", Component.literal("Heating"), heatingRateComponent, ChatFormatting.RED);
		addKeyValueTwoLiner("Cooling", Component.literal("Cooling"), coolingRateComponent, ChatFormatting.AQUA);

		if (!isHovered()) {
			addKeyValueTwoLiner("Mass", Component.literal("Mass"),
					GuiTextUtilities.formatConductivityToString(heatStorage.getMass()), ChatFormatting.GREEN);
		} else {
			addKeyValueTwoLiner("Specific Heat", Component.literal("Specific Heat"),
					GuiTextUtilities.formatConductivityToString(heatStorage.getSpecificHeat()), ChatFormatting.GREEN);
		}
	}
}
