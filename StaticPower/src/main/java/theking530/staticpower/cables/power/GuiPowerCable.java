package theking530.staticpower.cables.power;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.api.energy.metrics.MetricsTimeUnit;
import theking530.api.energy.metrics.PowerTransferMetrics.PowerTransferMetricWrapper;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.DataGraphWidget;
import theking530.staticcore.gui.widgets.DataGraphWidget.FloatGraphDataSet;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticcore.utilities.SDColor;

public class GuiPowerCable extends StaticCoreBlockEntityScreen<ContainerPowerCable, BlockEntityPowerCable> {

	private DataGraphWidget graphWidget;
	private MetricsTimeUnit displayType;
	private TextButton metricTypeButton;

	public GuiPowerCable(ContainerPowerCable container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 190, 125);
		displayType = MetricsTimeUnit.SECONDS;
	}

	@Override
	public void initializeGui() {
		this.registerWidget(new GuiPowerBarFromStorage(this.getTileEntity().powerCableComponent, 164, 20, 16, 78));
		this.registerWidget(graphWidget = new DataGraphWidget(10, 20, 146, 78));

		registerWidget(metricTypeButton = new TextButton(164, 102, 16, 16, "S", this::buttonPressed));
		metricTypeButton.setTooltip(Component.literal("Seconds"));
	}

	@Override
	public void updateData() {
		// Get the metrics.
		PowerTransferMetricWrapper metrics = getMetrics();
		if (metrics == null) {
			return;
		}

		// Allocate the metrics containers.
		List<Float> netData = new ArrayList<Float>();

		for (Float value : metrics.getInputValues()) {
			netData.add(value);
		}

		int index = 0;
		for (Float value : metrics.getOutputValues()) {
			netData.set(index, netData.get(index) - value);
			index++;
		}

		// Add the data.
		graphWidget.setDataSet("received", new FloatGraphDataSet(new SDColor(0, 1.0f, 0.2f, 0.75f), metrics.getInputValues()));
		graphWidget.setDataSet("provided", new FloatGraphDataSet(new SDColor(1.0f, 0, 0.1f, 0.75f), metrics.getOutputValues()));
		graphWidget.setDataSet("net", new FloatGraphDataSet(new SDColor(0, 0.1f, 1.0f, 1), netData));
	}

	protected PowerTransferMetricWrapper getMetrics() {
		return getMenu().getMetrics().getData(displayType);
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		if (displayType == MetricsTimeUnit.TICKS) {
			displayType = MetricsTimeUnit.SECONDS;
			metricTypeButton.setTooltip(Component.translatable("gui.staticpower.metric_minutes"));
			metricTypeButton.setText("S");
		} else if (displayType == MetricsTimeUnit.SECONDS) {
			displayType = MetricsTimeUnit.MINUTES;
			metricTypeButton.setTooltip(Component.translatable("gui.staticpower.metric_minutes"));
			metricTypeButton.setText("M");
		} else if (displayType == MetricsTimeUnit.MINUTES) {
			displayType = MetricsTimeUnit.HOURS;
			metricTypeButton.setTooltip(Component.translatable("gui.staticpower.metric_hours"));
			metricTypeButton.setText("H");
		} else if (displayType == MetricsTimeUnit.HOURS) {
			displayType = MetricsTimeUnit.TICKS;
			metricTypeButton.setTooltip(Component.translatable("gui.staticpower.metric_ticks"));
			metricTypeButton.setText("T");
		}
	}

	@Override
	protected void drawForegroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
//		float textScale = 0.70f;
//		float xPos = 10;
//		float yPos = 104;
//		float inPosition = xPos + 7;
//		float outPosition = xPos + 60;
//		float netPosition = xPos + 110;
//
//		// Draw the values background.
//		GuiDrawUtilities.drawSlot(stack, 146, 12, xPos, yPos, 0);
//
//		// Draw the labels.
//		GuiDrawUtilities.drawString(stack, "In:", inPosition, yPos + 4.5f, 0.0f, 0.5f, Color.EIGHT_BIT_DARK_GREY, false);
//		GuiDrawUtilities.drawString(stack, "Out:", outPosition, yPos + 4.5f, 0.0f, 0.5f, Color.EIGHT_BIT_DARK_GREY, false);
//		GuiDrawUtilities.drawString(stack, "Net:", netPosition, yPos + 4.5f, 0.0f, 0.5f, Color.EIGHT_BIT_DARK_GREY, false);
//
//		float recieve = this.getTileEntity().powerCableComponent.getClientLastEnergyReceieve();
//		String recivePerTick = GuiTextUtilities.formatEnergyRateToString(recieve).getString();
//		int recieveWidth = (int) (this.font.width(recivePerTick) * textScale);
//		GuiDrawUtilities.drawString(stack, recivePerTick, inPosition + 14 + (recieveWidth / 2), yPos + 9, 0.0f, textScale, new Color(0.0f, 255.0f, 50.0f), true);
//
//		float extract = -this.getTileEntity().powerCableComponent.getClientLastEnergyReceieve();
//		String extractPerTick = GuiTextUtilities.formatEnergyRateToString(extract).getString();
//		int extractWidth = (int) (this.font.width(extractPerTick) * textScale);
//		GuiDrawUtilities.drawString(stack, extractPerTick, outPosition + 14 + (extractWidth / 2), yPos + 9, 0.0f, textScale, new Color(255.0f, 0.0f, 30.0f), true);
//
//		float net = recieve + extract;
//		String netPerTick = GuiTextUtilities.formatEnergyRateToString(net).getString();
//		int netWidth = (int) (this.font.width(netPerTick) * textScale);
//		GuiDrawUtilities.drawString(stack, netPerTick, netPosition + 14 + (netWidth / 2), yPos + 9, 0.0f, textScale, new Color(0.0f, 100.0f, 255.0f), true);
	}
}
