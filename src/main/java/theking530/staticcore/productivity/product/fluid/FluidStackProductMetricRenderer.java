package theking530.staticcore.productivity.product.fluid;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.productivity.ProductMetricTileRenderer;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.init.ModProducts;

public class FluidStackProductMetricRenderer extends ProductMetricTileRenderer<FluidStack, ProductType<FluidStack>> {

	public FluidStackProductMetricRenderer() {
		super(ModProducts.Fluid.get());
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