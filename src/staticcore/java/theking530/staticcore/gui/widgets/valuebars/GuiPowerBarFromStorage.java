package theking530.staticcore.gui.widgets.valuebars;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.IStaticPowerStorage;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.math.Vector2D;

@OnlyIn(Dist.CLIENT)
public class GuiPowerBarFromStorage extends AbstractGuiWidget<GuiPowerBarFromStorage> {

	private IStaticPowerStorage energyStorage;

	public GuiPowerBarFromStorage(IStaticPowerStorage energyStorage, int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		this.energyStorage = energyStorage;
	}

	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		GuiPowerBarUtilities.drawPowerBar(matrix, 0, 0, getSize().getX(), getSize().getY(), energyStorage.getStoredPower(), energyStorage.getCapacity());
	}

	@Override
	public void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		tooltips.addAll(GuiPowerBarUtilities.getTooltip(energyStorage.getStoredPower(), energyStorage.getCapacity(), energyStorage.getMaximumPowerInput(),
				energyStorage.getInputVoltageRange().minimumVoltage(), energyStorage.getInputVoltageRange().maximumVoltage()));
	}
}
