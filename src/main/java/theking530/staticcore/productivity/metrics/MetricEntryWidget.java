package theking530.staticcore.productivity.metrics;

import java.util.List;

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
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class MetricEntryWidget extends AbstractGuiWidget<MetricEntryWidget> {
	@Nullable
	private SerializedMetricPeriod metric;
	private MetricType metricType;

	public MetricEntryWidget(@Nullable SerializedMetricPeriod metric, MetricType metricType, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.metric = metric;
		this.metricType = metricType;
	}

	public void updateWidgetBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX, int mouseY) {

	}

	public void setMetric(@Nullable SerializedMetricPeriod metric) {
		this.metric = metric;
	}

	public SerializedMetricPeriod getMetric() {
		return metric;
	}

	@Override
	public void renderWidgetBackground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		GuiDrawUtilities.drawRectangle(pose, getWidth(), 1, 0, 0, 1, new SDColor(1.0f, 1.0f, 1.0f, 0.5f));
		SDColor bgColor = new SDColor(0.6f, 0.6f, 0.6f, 1.0f);
		if (this.isHovered()) {
			bgColor.add(0, 0, 0.1f);
		}
		GuiDrawUtilities.drawRectangle(pose, getWidth(), getHeight() - 2, 0, 1, 1, bgColor);
		GuiDrawUtilities.drawRectangle(pose, getWidth(), 1, 0, getHeight() - 1, 1, new SDColor(0.0f, 0.0f, 0.0f, 0.5f));
	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {

	}

	@Override
	public void renderWidgetForeground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (metric == null) {
			return;
		}

		try {
			CompoundTag tag = TagParser.parseTag(metric.getSerializedProduct());
			tag.putByte("Count", (byte) 1);
			ItemStack product = ItemStack.of(tag);

			GuiDrawUtilities.drawItem(pose, product, 2, 2, 10, 16, 16);
			GuiDrawUtilities.drawStringLeftAligned(pose, GuiTextUtilities.formatNumberAsStringOneDecimal(metric.getMetric(metricType)).getString() + "/m", 21, 12f, 1, 0.75f,
					SDColor.EIGHT_BIT_WHITE, true);
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}

		float growthPercentage = 0.5f;
		pose.pushPose();
		pose.scale(1, 0.85f, 1);
		float barXPos = 48;
		float barYPos = (getHeight() - 12);
		float width = getWidth() / 5;
		GuiDrawUtilities.drawGenericBackground(pose, width, 7, barXPos, barYPos, 1, new SDColor(0.4f, 0.4f, 0.4f, 1.0f));

		GuiDrawUtilities.drawGenericBackground(pose, Math.max(7, width * growthPercentage), 7, barXPos, barYPos, 1, new SDColor(0.2f, 0.5f, 1.4f, 1.0f));
		pose.popPose();
	}

	public void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		super.getWidgetTooltips(mousePosition, tooltips, showAdvanced);
		try {
			CompoundTag tag = TagParser.parseTag(metric.getSerializedProduct());
			tag.putByte("Count", (byte) 1);
			ItemStack product = ItemStack.of(tag);
			tooltips.addAll(product.getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL));
		} catch (CommandSyntaxException e) {
			// TODO Auto-generated catch block
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
			return EInputResult.HANDLED;
		}

		return super.mouseClick(mouseX, mouseY, button);
	}
}
