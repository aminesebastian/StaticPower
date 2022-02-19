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
public class GuiHeatBarFromHeatStorage extends AbstractGuiWidget {

	private IHeatStorage heatStorage;

	public GuiHeatBarFromHeatStorage(IHeatStorage heatStorage, int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		this.heatStorage = heatStorage;
	}

	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		GuiHeatBarUtilities.drawHeatBar(matrix, getPosition().getX(), getPosition().getY() + getSize().getY(), getSize().getX(), getSize().getY(), 0.0f, heatStorage.getCurrentHeat(), heatStorage.getMaximumHeat());
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		tooltips.addAll(GuiHeatBarUtilities.getTooltip(heatStorage.getCurrentHeat(), heatStorage.getMaximumHeat(), heatStorage.getConductivity()));
	}
}
