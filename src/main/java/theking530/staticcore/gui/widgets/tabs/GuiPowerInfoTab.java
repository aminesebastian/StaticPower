package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
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
		super("Power I/O", new Color(242, 0, 255), 105, new Color(0.6f, 0.1f, 1.0f), new SpriteDrawable(GuiTextures.POWER_TAB_ICON, 16, 16));
		this.energyStorage = storage;
	}

	@Override
	public void tick() {
		super.tick();
		clear();
		addKeyValueTwoLiner("Input", new TextComponent("Current Input"), GuiTextUtilities.formatEnergyRateToString(energyStorage.getStorage().getReceivedPerTick()),
				ChatFormatting.GREEN);
		addKeyValueTwoLiner("Usage", new TextComponent("Current Usage"), GuiTextUtilities.formatEnergyRateToString(energyStorage.getStorage().getExtractedPerTick()),
				ChatFormatting.RED);
	}
}
