package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;

@OnlyIn(Dist.CLIENT)
public class GuiPowerInfoTab extends AbstractInfoTab {
	private EnergyStorageComponent energyStorage;

	public GuiPowerInfoTab(EnergyStorageComponent storage) {
		super("Power I/O", 83, GuiTextures.PURPLE_TAB, new SpriteDrawable(GuiTextures.POWER_TAB_ICON, 16, 16), new Color(242, 0, 255));
		this.energyStorage = storage;
	}

	@Override
	public void updateData() {
		super.updateData();
		clear();
		addKeyValueTwoLiner(new StringTextComponent("Current Input"), GuiTextUtilities.formatEnergyRateToString(energyStorage.getStorage().getRecievedPerTick()), TextFormatting.GREEN);
		addKeyValueTwoLiner(new StringTextComponent("Current Usage"), GuiTextUtilities.formatEnergyRateToString(energyStorage.getStorage().getExtractedPerTick()), TextFormatting.RED);
	}
}
