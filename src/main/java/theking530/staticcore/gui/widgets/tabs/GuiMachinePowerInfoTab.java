package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.utilities.StaticPowerEnergyTextUtilities;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.client.gui.GuiTextures;

@OnlyIn(Dist.CLIENT)
public class GuiMachinePowerInfoTab extends AbstractInfoTab {

	private PowerStorageComponent energyStorage;

	public GuiMachinePowerInfoTab(PowerStorageComponent storage) {
		super("Power I/O", new Color(242, 0, 255), 105, new Color(0.6f, 0.1f, 1.0f), new SpriteDrawable(GuiTextures.POWER_TAB_ICON, 16, 16));
		this.energyStorage = storage;
	}

	@Override
	public void tick() {
		super.tick();
		clear();
		addKeyValueTwoLiner("Input", new TextComponent("Power Input"), StaticPowerEnergyTextUtilities.formatPowerRateToString(energyStorage.getAveragePowerAddedPerTick()),
				ChatFormatting.GREEN);
		addKeyValueTwoLiner("Usage", new TextComponent("Power Usage"), StaticPowerEnergyTextUtilities.formatPowerRateToString(energyStorage.getAveragePowerUsedPerTick()),
				ChatFormatting.RED);
		addKeyValueTwoLiner("Max Voltage", new TextComponent("Input Voltage"), StaticPowerEnergyTextUtilities.formatVoltageRangeToString(energyStorage.getInputVoltageRange()),
				ChatFormatting.AQUA);
	}
}
