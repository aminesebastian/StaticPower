package theking530.staticcore.productivity;

import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.ProductionMetric;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public abstract class ProductMetricTileRenderer<T, K extends ProductType<T>> {
	private ProductionMetric metric;
	private MetricType metricType;
	private T deserializedProduct;

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
		this.deserializedProduct = this.deserializeProduct(metric.getSerializedProduct());
	}

	public void drawBackground(PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {

	}

	public void drawForeground(PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		drawIcon(deserializedProduct, pose, mousePosition, partialTicks, tileSize, isHovered);
		drawValue(deserializedProduct, pose, mousePosition, partialTicks, tileSize, isHovered);
		drawExtras(deserializedProduct, pose, mousePosition, partialTicks, tileSize, isHovered);
	}

	protected abstract void drawIcon(T product, PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered);

	protected void drawValue(T product, PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		GuiDrawUtilities.drawStringLeftAligned(pose, GuiTextUtilities.formatNumberAsStringOneDecimal(metric.getMetric(metricType)).getString() + "/m", 22, 12f, 1, 0.75f,
				SDColor.EIGHT_BIT_WHITE, true);
	}

	protected void drawExtras(T product, PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		SDColor testColor = new SDColor(1, 1, 1, 1);
		int serializedHash = metric.getSerializedProduct().hashCode();
		testColor.setRed(Math.abs(serializedHash) % 100 / 100.0f);
		testColor.setGreen(Math.abs(serializedHash) % 200 / 200.0f);
		testColor.setBlue(Math.abs(serializedHash) % 300 / 300.0f);
		testColor.desaturate(-1);
		testColor.lighten(0.5f, 0.5f, 0.5f, 0.0f);

		float growthPercentage = 0.5f;
		pose.pushPose();
		pose.scale(1, 0.85f, 1);
		float barXPos = 50;
		float barYPos = (tileSize.getY() - 12);
		float width = (tileSize.getX() - barXPos - 5);
		GuiDrawUtilities.drawGenericBackground(pose, width, 7, barXPos, barYPos, 1, new SDColor(0.4f, 0.4f, 0.4f, 1.0f));
		GuiDrawUtilities.drawGenericBackground(pose, Math.max(7, width * growthPercentage), 7, barXPos, barYPos, 1, testColor);
		pose.popPose();
	}

	protected abstract T deserializeProduct(String serializedProduct);
}
