package theking530.staticcore.productivity.product.power;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.productivity.ProductMetricTileRenderer;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.init.ModProducts;

public class PowerProductMetricRenderer extends ProductMetricTileRenderer<PowerProductionStack, ProductType<PowerProductionStack>> {

	public PowerProductMetricRenderer() {
		super(ModProducts.Power.get());
	}

	@Override
	protected void drawIcon(PowerProductionStack product, PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		GuiDrawUtilities.drawItem(pose, new ItemStack(product.getBlockSource().asItem()), 3, 2, 10, 16, 16);
	}

	@Override
	protected String getValueText(PowerProductionStack product, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		return PowerTextFormatting.formatPowerToString(getMetric().getMetricValue(getMetricType()).getCurrentValue()).getString();
	}
}