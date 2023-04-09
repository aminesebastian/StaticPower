package theking530.staticcore.productivity.product.power;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.productivity.ProductMetricTileRenderer;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.math.Vector2D;

public class PowerProductMetricRenderer extends ProductMetricTileRenderer<PowerProductInterfaceId, ProductType<PowerProductInterfaceId>> {

	public PowerProductMetricRenderer() {
		super(StaticCoreProductTypes.Power.get());
	}

	@Override
	protected void drawIcon(PowerProductInterfaceId product, PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		GuiDrawUtilities.drawItem(pose, new ItemStack(product.getBlockSource().asItem()), 3, 2, 10, 16, 16);
	}

	@Override
	protected String getValueText(PowerProductInterfaceId product, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		return PowerTextFormatting.formatPowerToString(getMetric().getMetricValue(getMetricType()).getCurrentValue()).getString();
	}
}
