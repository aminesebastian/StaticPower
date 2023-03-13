package theking530.staticcore.gui.widgets.valuebars;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Vector2D;

@OnlyIn(Dist.CLIENT)
public class GuiHeatBarFromHeatStorage extends AbstractGuiWidget<GuiHeatBarFromHeatStorage> {

	private IHeatStorage heatStorage;
	private int interpolatedHeat;

	public GuiHeatBarFromHeatStorage(IHeatStorage heatStorage, int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		this.heatStorage = heatStorage;
		interpolatedHeat = this.heatStorage.getCurrentHeat();
	}

	@Override
	public void tick() {
		super.tick();
		int delta = heatStorage.getCurrentHeat() - interpolatedHeat;
		float distance = interpolatedHeat - heatStorage.getCurrentHeat();
		float distanceRatio = distance / heatStorage.getCurrentHeat();
		int maxMovement = (int) (Math.abs(distanceRatio / 2) * distance);
		if (delta < 0) {
			delta = Math.max(delta, -maxMovement);
		} else {
			delta = Math.max(delta, maxMovement);
		}
		interpolatedHeat += delta;
	}

	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		GuiHeatBarUtilities.drawHeatBar(matrix, 0, 0, getSize().getX(), getSize().getY(), 0.0f, heatStorage.getCurrentHeat(), heatStorage.getMinimumHeatThreshold(),
				heatStorage.getOverheatThreshold(), heatStorage.getMaximumHeat());
	}

	@Override
	public void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		tooltips.addAll(GuiHeatBarUtilities.getTooltip(heatStorage.getCurrentHeat(), heatStorage.getMinimumHeatThreshold(), heatStorage.getOverheatThreshold(),
				heatStorage.getMaximumHeat(), heatStorage.getConductivity()));
	}
}
