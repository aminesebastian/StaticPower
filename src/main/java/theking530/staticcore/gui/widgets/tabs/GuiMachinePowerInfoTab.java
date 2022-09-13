package theking530.staticcore.gui.widgets.tabs;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;

@OnlyIn(Dist.CLIENT)
public class GuiMachinePowerInfoTab extends GuiPowerInfoTab {

	public GuiMachinePowerInfoTab(PowerStorageComponent storage) {
		super(storage);
		this.setOutputLabel("gui.staticpower.power_tab.power_usage");
		this.setMaxOutputLabel("gui.staticpower.power_tab.max_power_usage");
	}

	@Override
	protected void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		if (isOpen()) {
//			float topOffset = 2;
//
//			String avgAdded = PowerTextFormatting.formatPowerRateToString(energyStorage.getAveragePowerAddedPerTick()).getString();
//			GuiDrawUtilities.drawTexture(matrix, GuiTextures.DOWN_INDICATOR, 12, 12, 12, topOffset + 25f, Color.GREEN);
//			GuiDrawUtilities.drawStringLeftAligned(matrix, avgAdded, 25, topOffset + 34f, 1, 1, Color.EIGHT_BIT_WHITE, true);
//
//			String avgUsed = PowerTextFormatting.formatPowerRateToString(energyStorage.getAveragePowerUsedPerTick()).getString();
//			GuiDrawUtilities.drawTexture(matrix, GuiTextures.UP_INDICATOR, 12, 12, 57, topOffset + 25f, Color.RED);
//			GuiDrawUtilities.drawStringLeftAligned(matrix, avgUsed, 70, topOffset + 34f, 1, 1, Color.EIGHT_BIT_WHITE, true);
		}
	}

	@Override
	protected void renderWidgetForeground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {

	}

	@Override
	protected void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
	}
}
