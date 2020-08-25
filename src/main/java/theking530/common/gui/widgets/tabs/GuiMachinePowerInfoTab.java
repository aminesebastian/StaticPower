package theking530.common.gui.widgets.tabs;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.common.gui.GuiTextures;
import theking530.common.gui.drawables.SpriteDrawable;
import theking530.common.utilities.Color;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;

public class GuiMachinePowerInfoTab extends AbstractInfoTab {

	private EnergyStorageComponent energyStorage;
	@SuppressWarnings("unused")
	private MachineProcessingComponent processingComponent;

	public GuiMachinePowerInfoTab(EnergyStorageComponent storage, MachineProcessingComponent processingComponent) {
		super("Power I/O", 83, GuiTextures.PURPLE_TAB, new SpriteDrawable(GuiTextures.POWER_TAB_ICON, 16, 16), new Color(242, 0, 255));
		this.energyStorage = storage;
		this.processingComponent = processingComponent;
	}

	@Override
	public void updateData() {
		super.updateData();
		clear();
		addKeyValueTwoLiner(new StringTextComponent("Current Input"), GuiTextUtilities.formatEnergyRateToString(energyStorage.getStorage().getRecievedPerTick()), TextFormatting.GREEN);
		addKeyValueTwoLiner(new StringTextComponent("Current Usage"), GuiTextUtilities.formatEnergyRateToString(energyStorage.getStorage().getExtractedPerTick()), TextFormatting.RED);
		addKeyValueTwoLiner(new StringTextComponent("Max I/O"), GuiTextUtilities.formatEnergyRateToString(energyStorage.getStorage().getMaxDrain()), TextFormatting.AQUA);
	}
}
