package theking530.staticcore.productivity.client;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.productivity.ProductMetricTileRendererRegistry;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.ProductionMetric;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector2D;

public class MetricEntryWidget extends AbstractGuiWidget<MetricEntryWidget> {

	@Nullable
	private ProductionMetric metric;
	private MetricType metricType;
	private ProductType<?> currentProductType;

	private Consumer<MetricEntryWidget> clicked;

	public MetricEntryWidget(MetricType metricType, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.metricType = metricType;
	}

	public void setMetric(ProductType<?> productType, @Nullable ProductionMetric metric) {
		this.metric = metric;
		this.currentProductType = productType;
	}

	public ProductionMetric getMetric() {
		return metric;
	}

	public MetricType getMetricType() {
		return metricType;
	}

	public ProductType<?> getCurrentProductType() {
		return currentProductType;
	}

	public void updateWidgetBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX,
			int mouseY) {

	}

	public void setClickedCallback(Consumer<MetricEntryWidget> clicked) {
		this.clicked = clicked;
	}

	@Override
	public void renderWidgetBackground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		SDColor lightColor = new SDColor(0.75f, 0.75f, 0.75f, 1.0f);

		SDColor bgColor = new SDColor(0.6f, 0.6f, 0.6f, 1.0f);
		if (this.isHovered()) {
			bgColor.add(0, 0, 0.1f);
		}
		GuiDrawUtilities.drawRectangle(pose, getWidth(), getHeight(), 0, 0, 0, bgColor);

		GuiDrawUtilities.drawRectangle(pose, getWidth(), 1, 0, 0, 1, lightColor);
		GuiDrawUtilities.drawRectangle(pose, getWidth(), 1, 0, getHeight() - 1, 1, SDColor.DARK_GREY);

		GuiDrawUtilities.drawRectangle(pose, 1f, getHeight(), 0, 0, 10, SDColor.DARK_GREY);
		GuiDrawUtilities.drawRectangle(pose, 1f, getHeight() - 2, 1, 1, 10, SDColor.GREY);

		GuiDrawUtilities.drawRectangle(pose, 1f, getHeight() - 1, getWidth() - 2, 0, 10, lightColor);
		GuiDrawUtilities.drawRectangle(pose, 1f, getHeight(), getWidth() - 1, 0, 10, SDColor.DARK_GREY);

		if (metric != null && currentProductType != null) {
			ProductMetricTileRendererRegistry.getRenderer(currentProductType).drawBackground(metric, metricType,
					metric.getSmoothedMetricValue(metricType), pose, new Vector2D(mouseX, mouseY), partialTicks,
					getSize(), isHovered());
		}
	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
	}

	@Override
	public void renderWidgetForeground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (metric != null && currentProductType != null) {
			ProductMetricTileRendererRegistry.getRenderer(currentProductType).drawForeground(metric, metricType,
					metric.getSmoothedMetricValue(metricType), pose, new Vector2D(mouseX, mouseY), partialTicks,
					getSize(), isHovered());
		}
	}

	@SuppressWarnings("resource")
	public void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		super.getWidgetTooltips(mousePosition, tooltips, showAdvanced);
		try {
			if (metric != null) {
				if (currentProductType == StaticCoreProductTypes.Item.get()) {
					CompoundTag tag = TagParser.parseTag(metric.getSerializedProduct());
					tag.putByte("Count", (byte) 1);
					ItemStack product = ItemStack.of(tag);
					tooltips.addAll(
							product.getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL));
				} else if (currentProductType == StaticCoreProductTypes.Fluid.get()) {
					CompoundTag tag = TagParser.parseTag(metric.getSerializedProduct());
					tag.putInt("Amount", (byte) 1);
					FluidStack product = FluidStack.loadFluidStackFromNBT(tag);
					tooltips.add(product.getDisplayName());
				}
			}
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}

	}

	@Override
	public EInputResult mouseMove(double mouseX, double mouseY) {
		if (isHovered()) {
			return EInputResult.HANDLED;
		}
		return super.mouseMove(mouseX, mouseY);
	}

	@Override
	public EInputResult mouseReleased(double mouseX, double mouseY, int button) {
		if (isHovered()) {
			playSoundLocally(SoundEvents.STONE_BUTTON_CLICK_OFF, 1.0f, 2.0f);
			if (clicked != null) {
				clicked.accept(this);
			}
			return EInputResult.HANDLED;
		}

		return super.mouseClick(mouseX, mouseY, button);
	}
}
