package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;

@OnlyIn(Dist.CLIENT)
public class GuiMachinePowerInfoTab extends AbstractInfoTab {

	private EnergyStorageComponent energyStorage;

	public GuiMachinePowerInfoTab(EnergyStorageComponent storage) {
		super("Power I/O", new Color(242, 0, 255), 83, GuiTextures.PURPLE_TAB, new SpriteDrawable(GuiTextures.POWER_TAB_ICON, 16, 16));
		this.energyStorage = storage;
	}

	@Override
	public void updateData() {
		super.updateData();
		clear();
		addKeyValueTwoLiner("Input", new TextComponent("Current Input"), GuiTextUtilities.formatEnergyRateToString(energyStorage.getStorage().getReceivedPerTick()),
				ChatFormatting.GREEN);
		addKeyValueTwoLiner("Usage", new TextComponent("Current Usage"), GuiTextUtilities.formatEnergyRateToString(energyStorage.getStorage().getExtractedPerTick()),
				ChatFormatting.RED);
		addKeyValueTwoLiner("I/O", new TextComponent("Max I/O"), GuiTextUtilities.formatEnergyRateToString(energyStorage.getStorage().getMaxDrain()), ChatFormatting.AQUA);
	}
}
