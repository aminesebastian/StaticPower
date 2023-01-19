package theking530.staticcore.productivity.product.item;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.productivity.ProductMetricTileRenderer;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.init.ModProducts;
import theking530.staticpower.utilities.MetricConverter;

public class ItemStackProductMetricRenderer extends ProductMetricTileRenderer<ItemStack, ProductType<ItemStack>> {

	public ItemStackProductMetricRenderer() {
		super(ModProducts.Item.get());
	}

	@Override
	protected void drawIcon(ItemStack product, PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		GuiDrawUtilities.drawItem(pose, product, 3, 2, 10, 16, 16);
	}

	@Override
	protected String getValueText(ItemStack product, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		MetricConverter converter = new MetricConverter(getMetric().getMetricValue(getMetricType()).getCurrentValue());
		return converter.getValueAsString(true);
	}
}