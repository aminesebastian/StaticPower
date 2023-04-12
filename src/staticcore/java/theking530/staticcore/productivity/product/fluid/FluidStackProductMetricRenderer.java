package theking530.staticcore.productivity.product.fluid;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.productivity.cacheentry.ProductivityRate;
import theking530.staticcore.productivity.client.ProductMetricTileRenderer;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.ProductionMetric;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.math.Vector2D;

public class FluidStackProductMetricRenderer extends ProductMetricTileRenderer<FluidStack, ProductType<FluidStack>> {

	public FluidStackProductMetricRenderer() {
		super(StaticCoreProductTypes.Fluid.get());
	}

	@Override
	protected void drawIcon(ProductionMetric metric, MetricType metricType, FluidStack product,
			ProductivityRate smoothedValue, PoseStack pose, Vector2D mousePosition, float partialTicks,
			Vector2D tileSize, boolean isHovered) {
		GuiFluidBarUtilities.drawFluidBar(pose, product, 2, 1, 3, 17, 10, 15, 15, false);
	}

	@Override
	protected String getValueText(ProductionMetric metric, MetricType metricType, FluidStack product,
			ProductivityRate smoothedValue, Vector2D mousePosition, float partialTicks, Vector2D tileSize,
			boolean isHovered) {
		return GuiTextUtilities.formatFluidToString(smoothedValue.getCurrentValue()).getString();
	}
}
