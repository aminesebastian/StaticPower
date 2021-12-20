package theking530.staticpower.cables.power;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.DataGraphWidget;
import theking530.staticcore.gui.widgets.DataGraphWidget.FloatGraphDataSet;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.power.PowerTransferMetrics.MetricCategory;
import theking530.staticpower.tileentities.components.power.PowerTransferMetrics.PowerTransferMetricWrapper;

public class GuiPowerCable extends StaticPowerTileEntityGui<ContainerPowerCable, TileEntityPowerCable> {

	private DataGraphWidget graphWidget;
	private MetricCategory displayType;
	private TextButton metricTypeButton;

	public GuiPowerCable(ContainerPowerCable container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 190, 125);
		displayType = MetricCategory.SECONDS;
	}

	@Override
	public void initializeGui() {
		this.registerWidget(new GuiPowerBarFromEnergyStorage(this.getTileEntity().powerCableComponent, 164, 20, 16, 78));
		this.registerWidget(graphWidget = new DataGraphWidget(10, 20, 146, 78));

		registerWidget(metricTypeButton = new TextButton(164, 102, 16, 16, "S", this::buttonPressed));
		metricTypeButton.setTooltip(new StringTextComponent("Seconds"));
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
		graphWidget.setDataSet("received", new FloatGraphDataSet(new Color(0, 1.0f, 0.2f, 0.75f), metrics.getInputValues()));
		graphWidget.setDataSet("provided", new FloatGraphDataSet(new Color(1.0f, 0, 0.1f, 0.75f), metrics.getOutputValues()));
		graphWidget.setDataSet("net", new FloatGraphDataSet(new Color(0, 0.1f, 1.0f, 1), netData));
	}

	protected PowerTransferMetricWrapper getMetrics() {
		return getContainer().getMetrics().getData(displayType);
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		if (displayType == MetricCategory.TICKS) {
			displayType = MetricCategory.SECONDS;
			metricTypeButton.setTooltip(new TranslationTextComponent("gui.staticpower.metric_minutes"));
			metricTypeButton.setText("S");
		} else if (displayType == MetricCategory.SECONDS) {
			displayType = MetricCategory.MINUTES;
			metricTypeButton.setTooltip(new TranslationTextComponent("gui.staticpower.metric_minutes"));
			metricTypeButton.setText("M");
		} else if (displayType == MetricCategory.MINUTES) {
			displayType = MetricCategory.HOURS;
			metricTypeButton.setTooltip(new TranslationTextComponent("gui.staticpower.metric_hours"));
			metricTypeButton.setText("H");
		} else if (displayType == MetricCategory.HOURS) {
			displayType = MetricCategory.TICKS;
			metricTypeButton.setTooltip(new TranslationTextComponent("gui.staticpower.metric_ticks"));
			metricTypeButton.setText("T");
		}
	}

	@Override
	protected void drawForegroundExtras(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		float textScale = 0.70f;
		float xPos = 10;
		float yPos = 104;
		float inPosition = xPos + 7;
		float outPosition = xPos + 60;
		float netPosition = xPos + 110;

		// Draw the values background.
		GuiDrawUtilities.drawSlot(stack, xPos, yPos, 146, 12, 0);

		// Draw the labels.
		GuiDrawUtilities.drawStringWithSize(stack, "In:", inPosition, yPos + 4.5f, 0.5f, Color.EIGHT_BIT_DARK_GREY, false);
		GuiDrawUtilities.drawStringWithSize(stack, "Out:", outPosition, yPos + 4.5f, 0.5f, Color.EIGHT_BIT_DARK_GREY, false);
		GuiDrawUtilities.drawStringWithSize(stack, "Net:", netPosition, yPos + 4.5f, 0.5f, Color.EIGHT_BIT_DARK_GREY, false);

		float recieve = this.getTileEntity().powerCableComponent.getClientLastEnergyReceieve();
		String recivePerTick = GuiTextUtilities.formatEnergyRateToString(recieve).getString();
		int recieveWidth = (int) (this.font.getStringWidth(recivePerTick) * textScale);
		GuiDrawUtilities.drawStringWithSize(stack, recivePerTick, inPosition + 14 + (recieveWidth / 2), yPos + 9, textScale, new Color(0.0f, 255.0f, 50.0f), true);

		float extract = -this.getTileEntity().powerCableComponent.getClientLastEnergyReceieve();
		String extractPerTick = GuiTextUtilities.formatEnergyRateToString(extract).getString();
		int extractWidth = (int) (this.font.getStringWidth(extractPerTick) * textScale);
		GuiDrawUtilities.drawStringWithSize(stack, extractPerTick, outPosition + 14 + (extractWidth / 2), yPos + 9, textScale, new Color(255.0f, 0.0f, 30.0f), true);

		float net = recieve + extract;
		String netPerTick = GuiTextUtilities.formatEnergyRateToString(net).getString();
		int netWidth = (int) (this.font.getStringWidth(netPerTick) * textScale);
		GuiDrawUtilities.drawStringWithSize(stack, netPerTick, netPosition + 14 + (netWidth / 2), yPos + 9, textScale, new Color(0.0f, 100.0f, 255.0f), true);
	}
}
