package theking530.staticcore.productivity.product.fluid;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiTextUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.productivity.ProductMetricTileRenderer;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.math.Vector2D;

public class FluidStackProductMetricRenderer extends ProductMetricTileRenderer<FluidStack, ProductType<FluidStack>> {

	public FluidStackProductMetricRenderer() {
		super(StaticCoreProductTypes.Fluid.get());
	}

	@Override
	protected void drawIcon(FluidStack product, PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		GuiFluidBarUtilities.drawFluidBar(pose, product, 2, 1, 3, 17, 10, 15, 15, false);
	}

	@Override
	protected String getValueText(FluidStack product, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		return GuiTextUtilities.formatFluidToString(getMetric().getMetricValue(getMetricType()).getCurrentValue()).getString();
	}
}
