package theking530.staticcore.productivity.client;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.productivity.cacheentry.ProductivityRate;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.ProductionMetric;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector2D;

public abstract class ProductMetricTileRenderer<T, K extends ProductType<T>> {
	private K productType;

	public ProductMetricTileRenderer(K productType) {
		this.productType = productType;
	}

	public K getProductType() {
		return productType;
	}

	public T deserializeProduct(ProductionMetric metric) {
		return productType.deserializeProduct(metric.getSerializedProduct());
	}

	public void drawBackground(ProductionMetric metric, MetricType metricType, ProductivityRate smoothedValue,
			PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {

	}

	public void getTooltips(ProductionMetric metric, MetricType metricType, ProductivityRate smoothedValue,
			PoseStack pose, Vector2D mousePosition, Vector2D tileSize, boolean isHovered, List<Component> tooltips,
			boolean showAdvanced) {
		T deserializedProduct = deserializeProduct(metric);
		getTooltips(metric, metricType, deserializedProduct, smoothedValue, pose, mousePosition, tileSize, isHovered,
				tooltips, showAdvanced);
	}

	public void drawForeground(ProductionMetric metric, MetricType metricType, ProductivityRate smoothedValue,
			PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		T deserializedProduct = deserializeProduct(metric);
		drawIcon(metric, metricType, deserializedProduct, smoothedValue, pose, mousePosition, partialTicks, tileSize,
				isHovered);
		drawValueText(metric, metricType, deserializedProduct, smoothedValue, pose, mousePosition, partialTicks,
				tileSize, isHovered);
		drawExtras(metric, metricType, deserializedProduct, smoothedValue, pose, mousePosition, partialTicks, tileSize,
				isHovered);
	}

	protected abstract String getValueText(ProductionMetric metric, MetricType metricType, T product,
			ProductivityRate smoothedValue, Vector2D mousePosition, float partialTicks, Vector2D tileSize,
			boolean isHovered);

	protected abstract void drawIcon(ProductionMetric metric, MetricType metricType, T product,
			ProductivityRate smoothedValue, PoseStack pose, Vector2D mousePosition, float partialTicks,
			Vector2D tileSize, boolean isHovered);

	protected void drawValueText(ProductionMetric metric, MetricType metricType, T product,
			ProductivityRate smoothedValue, PoseStack pose, Vector2D mousePosition, float partialTicks,
			Vector2D tileSize, boolean isHovered) {
		String valueText = getValueText(metric, metricType, product, smoothedValue, tileSize, partialTicks, tileSize,
				isHovered);
		GuiDrawUtilities.drawStringLeftAligned(pose, valueText + "/m", 22, 12f, 1, 0.75f, SDColor.EIGHT_BIT_WHITE,
				true);
	}

	protected void drawExtras(ProductionMetric metric, MetricType metricType, T product, ProductivityRate smoothedValue,
			PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		SDColor productColor = productType.getProductColor(product);
		ProductivityRate rate = metric.getMetricValue(metricType);
		float growthPercentage = 0;
		if (rate.getIdealValue() > 0) {
			growthPercentage = (float) (rate.getCurrentValue() / rate.getIdealValue());
		}

		// Scale the width by .75 since we scale it by that much when rendering.
		String valueText = getValueText(metric, metricType, product, smoothedValue, mousePosition, partialTicks,
				tileSize, isHovered);
		@SuppressWarnings("resource")
		float valueWidth = Minecraft.getInstance().font.width(valueText) * 0.75f;
		valueWidth += 10;

		pose.pushPose();
		pose.scale(1, 0.85f, 1);
		float barXPos = valueWidth + 25;
		float barYPos = (tileSize.getY() - 12);
		float width = (tileSize.getX() - barXPos - 5);
		GuiDrawUtilities.drawGenericBackground(pose, width, 7, barXPos, barYPos, 1,
				new SDColor(0.4f, 0.4f, 0.4f, 1.0f));
		GuiDrawUtilities.drawGenericBackground(pose, Math.max(7, width * growthPercentage), 7, barXPos, barYPos, 1,
				productColor);
		pose.popPose();
	}

	protected void getTooltips(ProductionMetric metric, MetricType metricType, T product,
			ProductivityRate smoothedValue, PoseStack pose, Vector2D mousePosition, Vector2D tileSize,
			boolean isHovered, List<Component> tooltips, boolean showAdvanced) {

	}
}
