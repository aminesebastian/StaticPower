package theking530.staticcore.productivity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.productivity.cacheentry.ProductivityRate;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.ProductionMetric;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;

public abstract class ProductMetricTileRenderer<T, K extends ProductType<T>> {
	private ProductionMetric metric;
	private MetricType metricType;
	private T deserializedProduct;
	private K productType;

	public ProductMetricTileRenderer(K productType) {
		this.productType = productType;
	}

	public K getProductType() {
		return productType;
	}

	public ProductionMetric getMetric() {
		return metric;
	}

	public MetricType getMetricType() {
		return metricType;
	}

	public T getProduct() {
		return deserializedProduct;
	}

	public void setRenderContext(ProductionMetric metric, MetricType metricType) {
		this.metric = metric;
		this.metricType = metricType;
		this.deserializedProduct = productType.deserializeProduct(metric.getSerializedProduct());
	}

	public void drawBackground(PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {

	}

	public void drawForeground(PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		drawIcon(deserializedProduct, pose, mousePosition, partialTicks, tileSize, isHovered);
		drawValueText(deserializedProduct, pose, mousePosition, partialTicks, tileSize, isHovered);
		drawExtras(deserializedProduct, pose, mousePosition, partialTicks, tileSize, isHovered);
	}

	protected abstract String getValueText(T product, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered);

	protected abstract void drawIcon(T product, PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered);

	protected void drawValueText(T product, PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		String valueText = getValueText(product, tileSize, partialTicks, tileSize, isHovered);
		GuiDrawUtilities.drawStringLeftAligned(pose, valueText + "/m", 22, 12f, 1, 0.75f, SDColor.EIGHT_BIT_WHITE, true);
	}

	protected void drawExtras(T product, PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		SDColor productColor = productType.getProductColor(product);
		ProductivityRate rate = this.getMetric().getMetricValue(getMetricType());
		float growthPercentage = 0;
		if (rate.getIdealValue() > 0) {
			growthPercentage = (float) (rate.getCurrentValue() / rate.getIdealValue());
		}
		
		// Scale the width by .75 since we scale it by that much when rendering.
		String valueText = getValueText(product, mousePosition, partialTicks, tileSize, isHovered);
		@SuppressWarnings("resource")
		float valueWidth = Minecraft.getInstance().font.width(valueText) * 0.75f;
		valueWidth += 10;
		
		pose.pushPose();
		pose.scale(1, 0.85f, 1);
		float barXPos = valueWidth + 25;
		float barYPos = (tileSize.getY() - 12);
		float width = (tileSize.getX() - barXPos - 5);
		GuiDrawUtilities.drawGenericBackground(pose, width, 7, barXPos, barYPos, 1, new SDColor(0.4f, 0.4f, 0.4f, 1.0f));
		GuiDrawUtilities.drawGenericBackground(pose, Math.max(7, width * growthPercentage), 7, barXPos, barYPos, 1, productColor);
		pose.popPose();
	}
}
